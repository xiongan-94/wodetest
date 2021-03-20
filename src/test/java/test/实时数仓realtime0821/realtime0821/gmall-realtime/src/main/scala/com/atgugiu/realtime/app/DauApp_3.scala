package com.atgugiu.realtime.app

import java.time.LocalDate

import com.atgugiu.realtime.BaseAppV1
import com.atgugiu.realtime.bean.StartupLog
import com.atgugiu.realtime.util.{MyESUtil, MyRedisUtil}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka010.OffsetRange
import org.json4s.JsonAST.JObject
import org.json4s.jackson.JsonMethods

import scala.collection.mutable.ListBuffer

object DauApp_3 extends BaseAppV1 {
    override val master: String = "local[2]"
    override val appName: String = "DauApp_3"
    override val batchTime: Long = 5
    override val groupId: String = "DauApp_3"
    override val topics: Set[String] = Set("gmall_start_topic")

    override def run(ssc: StreamingContext,
                     sourceStream: DStream[ConsumerRecord[String, String]],
                     offsets: ListBuffer[OffsetRange]): Unit = {

        val startupLogStream = parseToStartupLog(sourceStream)
        val distinctedStream: DStream[StartupLog] = distinct_2(startupLogStream)

        distinctedStream.foreachRDD(rdd => {
            // 保存数据
            rdd.foreachPartition((it: Iterator[StartupLog]) => {
                val index = s"gmall_dau_info_${LocalDate.now().toString}"
                MyESUtil.insertBulk(index, it.map(log => (s"${log.mid}_${log.logDate}", log)))
            })
            saveOffset(offsets)
        })


    }


    def parseToStartupLog(sourceStream: DStream[ConsumerRecord[String, String]]) = {
        sourceStream.map((record: ConsumerRecord[String, String]) => {
            val jsonString = record.value()
            val jv = JsonMethods.parse(jsonString)
            val jCommon = jv \ "common"
            val JTs = jv \ "ts"
            implicit val f = org.json4s.DefaultFormats
            jCommon.merge(JObject("ts" -> JTs)).extract[StartupLog]

        })

    }


    def distinct_2(startupLogStream: DStream[StartupLog]) = {
        startupLogStream.mapPartitions(it => {
            val client = MyRedisUtil.getClient()
            val result = it.filter(log => {
                val setKey = s"dau:uids:${log.logDate}"
                val r = 1 == client.sadd(setKey, log.mid)
                client.expire(setKey, 60 * 60 * 24)
                r
            })
            client.close()
            result
        })
    }
}
