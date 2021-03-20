package com.atguigu.spark.spark_streaming.source

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}

object Spark01_Source_Kafka {
    
    val kafkaParams = Map[String, Object](
        "bootstrap.servers" -> "hadoop162:9092,hadoop163:9092,hadoop164:9092",
        "key.deserializer" -> classOf[StringDeserializer],
        "value.deserializer" -> classOf[StringDeserializer],
        "group.id" -> "Spark01_Source_Kafka",
        "auto.offset.reset" -> "latest",
        "enable.auto.commit" -> (true: java.lang.Boolean)
    )
    
    def main(args: Array[String]): Unit = {
        val conf = new SparkConf().setMaster("local[2]").setAppName("Spark01_Source_Kafka")
        val ssc = new StreamingContext(conf, Seconds(5))
        
        val sourceStream = KafkaUtils.createDirectStream[String, String](
            ssc,
            PreferConsistent,
            Subscribe[String, String](Array("spark0821"), kafkaParams)
        )
        
        sourceStream
            .map(_.value())
            .flatMap(_.split(" "))
            .map((_, 1))
            .reduceByKey(_ + _)
            .print(100)
        
        ssc.start()
        ssc.awaitTermination()
        
        
    }
}
