package com.atgugiu.realtime.app

import java.time.LocalDate

import com.atgugiu.realtime.bean.StartupLog
import com.atgugiu.realtime.util.{MyESUtil, MyKafkaUtil, MyRedisUtil, OffsetManager}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.json4s.JsonAST.JObject
import org.json4s.jackson.JsonMethods

import scala.collection.mutable.ListBuffer

/**
 * Author atguigu
 * Date 2021/1/5 9:02
 */
object DauApp_2 {

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

    def main(args: Array[String]): Unit = {

        val conf = new SparkConf().setMaster("local[2]").setAppName("DauApp")
        val ssc = new StreamingContext(conf, Seconds(5))

        val groupId = "DauApp_2"
        val topics = Set("gmall_start_topic");
        // 先从redis读取这次应该从什么位置开始消费  只需要读一次, 系统启动的时候
        val fromOffsets = OffsetManager.readOffset(groupId, topics)
        System.out.println("初始化偏移量:" + fromOffsets);
        // 存储每次消费的偏移量, 保存完数据之后, 再去保存这里的偏移量
        val offsets = ListBuffer.empty[OffsetRange]

        val sourceStream = MyKafkaUtil
            .getStreamFromKafka(ssc, groupId, topics, fromOffsets, isOne = true)("all")
            .transform(rdd => { //
                offsets.clear() // 每个批次更新偏移量记录
                val ranges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
                offsets ++= ranges
                rdd
            })

        val startupLogStream = parseToStartupLog(sourceStream)
        val distinctedStream: DStream[StartupLog] = distinct_2(startupLogStream)

        distinctedStream.foreachRDD(rdd => {
            // 保存数据
            rdd.foreachPartition((it: Iterator[StartupLog]) => {
                val index = s"gmall_dau_info_${LocalDate.now().toString}"
                MyESUtil.insertBulk(index, it.map(log => (s"${log.mid}_${log.logDate}", log)))
            })
            // 保存offset: 需要知道这个批次消费到了位置
            OffsetManager.saveOffsets(groupId, offsets)
        })

        ssc.start()

        ssc.awaitTermination()

    }


}
