package com.atgugiu.realtime.app

import java.time.LocalDate

import com.atgugiu.realtime.bean.StartupLog
import com.atgugiu.realtime.util.{MyESUtil, MyKafkaUtil, MyRedisUtil}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.json4s.JsonAST.JObject
import org.json4s.jackson.JsonMethods

/**
 * Author atguigu
 * Date 2021/1/5 9:02
 */
object DauApp {

    def parseToStartupLog(sourceStream: InputDStream[ConsumerRecord[String, String]]) = {
        sourceStream.map((record: ConsumerRecord[String, String]) => {
            val jsonString = record.value()
            val jv = JsonMethods.parse(jsonString)
            val jCommon = jv \ "common"
            val JTs = jv \ "ts"
            implicit val f = org.json4s.DefaultFormats
            jCommon.merge(JObject("ts" -> JTs)).extract[StartupLog]

        })

    }

    // 去重
    def distinct(startupLogStream: DStream[StartupLog]) = {
        // 问题: 性能问题. 每条数据创建一个到redis的连接, 对性能有影响
        startupLogStream.filter(log => {
            println("去重前: " + log)
            // 把这条日志中的设备id存入到redis的set中,如果返回1这条记录保留, 如果返回0就过滤掉
            val client = MyRedisUtil.getClient()
            // set集合: 应该每天一个, 存储当天的启动的设备id
            val setKey = s"dau:uids:${log.logDate}"
            val r = 1 == client.sadd(setKey, log.mid)
            client.close()
            r
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
        /*startupLogStream.transform(rdd => {

        }*/

    }

    def main(args: Array[String]): Unit = {
        // 1. 创建一个 StreamingContext
        val conf = new SparkConf().setMaster("local[2]").setAppName("DauApp")
        val ssc = new StreamingContext(conf, Seconds(5))
        // 2. 使用KafkaUtil 获取一个流
        val sourceStream = MyKafkaUtil
            .getStreamFromKafka(ssc, "DauApp", Set("gmall_start_topic"))("all")

        // 3. 对流做各种处理
        // 3.1 把数据封装到样例类中
        val startupLogStream = parseToStartupLog(sourceStream)
        // 3.2 对数据去重: 对每个设备, 只保留当天的第一次启动记录
        //        val distinctedStream = distinct(startupLogStream)
        val distinctedStream: DStream[StartupLog] = distinct_2(startupLogStream)
        // 3.3 数据写入到外部存储: es
        /*distinctedStream.foreachRDD(rdd => {
            rdd.foreachPartition((it: Iterator[StartupLog]) => {
                // 连接es
                // 写数据
                //关闭es
                val index = s"gmall_dau_info_${LocalDate.now().toString}"
                MyESUtil.insertBulk(index, it.map(log => (s"${log.mid}_${log.logDate}", log)))
            })
        })*/
        val index = s"gmall_dau_info_${LocalDate.now().toString}"
        distinctedStream.saveToES(index)


        // 4. 启动StreamingContext
        ssc.start()
        // 5. 阻止主线程退出
        ssc.awaitTermination()

    }

    implicit class RichEs(stream: DStream[StartupLog]) {
        def saveToES(index: String) = {
            stream.foreachRDD(rdd => {
                rdd.foreachPartition((it: Iterator[StartupLog]) => {
                    // 连接es
                    // 写数据
                    //关闭es
                    val index = s"gmall_dau_info_${LocalDate.now().toString}"
                    MyESUtil.insertBulk(index, it.map(log => (s"${log.mid}_${log.logDate}", log)))
                })
            })
        }
    }

}
