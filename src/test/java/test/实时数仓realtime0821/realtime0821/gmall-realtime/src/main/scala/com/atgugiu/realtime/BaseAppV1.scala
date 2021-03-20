package com.atgugiu.realtime

import com.atgugiu.realtime.util.{MyKafkaUtil, OffsetManager}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable.ListBuffer

abstract class BaseAppV1 {
    val master: String
    val appName: String
    val batchTime: Long
    val groupId: String
    val topics: Set[String]
    val offsetWhere : String = "redis"

    def main(args: Array[String]): Unit = {
        val conf = new SparkConf().setMaster(master).setAppName(appName)
        val ssc: StreamingContext = new StreamingContext(conf, Seconds(batchTime))
        // 先从redis读取这次应该从什么位置开始消费  只需要读一次, 系统启动的时候
        val fromOffsets = OffsetManager.readOffset(groupId, topics, offsetWhere)
        System.out.println("初始化偏移量:" + fromOffsets);
        // 存储每次消费的偏移量, 保存完数据之后, 再去保存这里的偏移量
        val offsets: ListBuffer[OffsetRange] = ListBuffer.empty[OffsetRange]

        val sourceStream: DStream[ConsumerRecord[String, String]] = MyKafkaUtil
            .getStreamFromKafka(ssc, groupId, topics, fromOffsets, isOne = true)("all")
            .transform(rdd => { //
                offsets.clear() // 每个批次更新偏移量记录
                val ranges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
                offsets ++= ranges
                rdd
            })

        // 谁继承这个类, 谁负责写
        run(ssc, sourceStream, offsets)
        ssc.start()
        ssc.awaitTermination()

    }

    def saveOffset(offsets: ListBuffer[OffsetRange]): Unit = {
        OffsetManager.saveOffsets(groupId, offsets)
    }

    def run(ssc: StreamingContext,
            sourceStream: DStream[ConsumerRecord[String, String]],
            offsets: ListBuffer[OffsetRange]): Unit
}
