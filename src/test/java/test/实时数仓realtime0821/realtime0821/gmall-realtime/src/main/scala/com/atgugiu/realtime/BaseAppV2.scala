package com.atgugiu.realtime

import com.atgugiu.realtime.util.{MyKafkaUtil, OffsetManager}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable.ListBuffer

abstract class BaseAppV2 {
    val master: String
    val appName: String
    val batchTime: Long
    val groupId: String
    val topics: Set[String]

    def main(args: Array[String]): Unit = {
        val conf = new SparkConf().setMaster(master).setAppName(appName)
        val ssc: StreamingContext = new StreamingContext(conf, Seconds(batchTime))
        // 涉及到多个topic的分区的偏移量信息
        val fromOffsets = OffsetManager.readOffset(groupId, topics)
        System.out.println("初始化偏移量:" + fromOffsets);
        // 因为是多个topic, 所以需要多个ListBuffer来存储给他们的偏移量
        val offsets: Map[String, ListBuffer[OffsetRange]] = topics
            .map(topic => {
                (topic, ListBuffer[OffsetRange]())
            })
            .toMap

        val sourceStreams = MyKafkaUtil
            .getStreamFromKafka(ssc, groupId, topics, fromOffsets, isOne = false) // 获取多个流
            .map {
                case (topic, stream) =>
                    val result: DStream[ConsumerRecord[String, String]] = stream
                        .transform(rdd => {
                            // 每个topic的数据
                            offsets(topic).clear()
                            val ranges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
                            offsets(topic) ++= ranges
                            rdd
                        })
                    (topic, result)
            }


        run(ssc, sourceStreams, offsets)
        ssc.start()
        ssc.awaitTermination()

    }

    def saveOffset(offsets: Map[String, ListBuffer[OffsetRange]]): Unit = {
        // 本来有多个ListBuffer, 把多个ListBuffer的值, 放在一个
        val buffers = offsets.values.reduce(_ ++ _)
        OffsetManager.saveOffsets(groupId, buffers)
    }

    def run(ssc: StreamingContext,
            sourceStream: Map[String, DStream[ConsumerRecord[String, String]]],
            offsets: Map[String, ListBuffer[OffsetRange]]): Unit
}
