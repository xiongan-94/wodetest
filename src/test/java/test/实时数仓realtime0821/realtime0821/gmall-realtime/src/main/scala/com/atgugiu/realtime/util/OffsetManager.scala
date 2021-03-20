package com.atgugiu.realtime.util

import org.apache.kafka.common.TopicPartition
import org.apache.spark.streaming.kafka010.OffsetRange

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

/**
 * Author atguigu
 * Date 2021/1/8 15:11
 */
object OffsetManager {


    /*

     */
    def readOffset(groupId: String,
                   topics: Set[String],
                   where: String = "redis"): collection.Map[TopicPartition, Long] = {
        where match {
            case "redis" =>
                val key = s"offset:${groupId}"
                val client = MyRedisUtil.getClient()
                val result = client
                    .hgetAll(key)
                    .asScala
                    .map {
                        case (tp, offset) =>
                            val split = tp.split(":")
                            new TopicPartition(split(0), split(1).toInt) -> offset.toLong
                    }
                client.close()
                result
            case "mysql" =>
                val url = "jdbc:mysql://hadoop162:3306/gmall_result?user=root&password=aaaaaa&useSSL=false"
                val sql = "select * from ads_offset where group_id=?"
                val list = MyJDBCUtil.readFromJdbc(url, sql, Seq(groupId))
                list
                    .map(map => {
                        val topic = map("topic").toString
                        val partition = map("partition_id").toString.toInt
                        val offset = map("partition_offset").toString.toLong
                        new TopicPartition(topic, partition) -> offset
                    })
                    .toMap
        }
    }

    def saveOffsets(groupId: String, offsets: ListBuffer[OffsetRange]): Unit = {
        val key = s"offset:${groupId}"
        val client = MyRedisUtil.getClient()
        val map = offsets
            .map(offsetRange => {
                val field = s"${offsetRange.topic}:${offsetRange.partition}"
                val offset = offsetRange.untilOffset // 下次应该从这个位置消费
                field -> offset.toString
            })
            .toMap
            .asJava
        client.hmset(key, map)
        System.out.println("保存偏移量: " + map);
        client.close()
    }

}
