package com.atguigu.spark.project.util

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent

/**
 * Author atguigu
 * Date 2020/12/23 15:26
 */
object MyKafkaUtil {
    var kafkaParams = Map[String, Object](
        "bootstrap.servers" -> "hadoop162:9092,hadoop163:9092,hadoop164:9092",
        "key.deserializer" -> classOf[StringDeserializer],
        "value.deserializer" -> classOf[StringDeserializer],
        "auto.offset.reset" -> "latest",
        "enable.auto.commit" -> (true: java.lang.Boolean)
    )
    
    def getKafkaStream(ssc: StreamingContext,
                       groupId: String,
                       topic: String) = {
        
        kafkaParams += "group.id" -> groupId
        
        KafkaUtils
            .createDirectStream[String, String](
                ssc,
                PreferConsistent,
                Subscribe[String, String](Array(topic), kafkaParams)
            )
            .map(_.value())
    }
}
