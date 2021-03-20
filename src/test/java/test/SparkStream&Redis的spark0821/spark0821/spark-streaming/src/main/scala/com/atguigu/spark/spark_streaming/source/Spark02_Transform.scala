package com.atguigu.spark.spark_streaming.source

import com.atguigu.spark.spark_streaming.source.Spark01_Source_Kafka.kafkaParams
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Author atguigu
 * Date 2020/12/23 10:17
 */
case class User(age: Int)

object Spark02_Transform {
    def main(args: Array[String]): Unit = {
        val conf = new SparkConf().setMaster("local[2]").setAppName("Spark01_Source_Kafka")
        val ssc = new StreamingContext(conf, Seconds(5))
        
        val sourceStream = KafkaUtils.createDirectStream[String, String](
            ssc,
            PreferConsistent,
            Subscribe[String, String](Array("spark0821"), kafkaParams)
        )
        val user: User = User(20)
        // 代码1: driver  只会执行
        println(s"driver ... 执行一次: ${System.identityHashCode(user)}")
        // 转换算子: 懒执行
        val wordCount = sourceStream.transform((rdd: RDD[ConsumerRecord[String, String]]) => {
            // 代码2: driver 每个批次执行一次
            println(s"driver ... 每个批次执行一次: ${System.identityHashCode(user)}")
            rdd
                .flatMap(_.value().split(" "))
                .map(word => {
                    // 代码3: executor
                    println(s"executor ... 每个单词执行一次:  ${System.identityHashCode(user)}")
                    (word, 1)
                })
                .reduceByKey(_ + _)
        })
        wordCount.print()
        
        ssc.start()
        ssc.awaitTermination()
        
    }
}

/*

rdd:
    如果是在算子的匿名函数内执行的是在executor, 其他的都是在driver
    
stream:
    transform 和 foreachRDD 传给他们的匿名函数的代码是在driver, 而且每个批次执行一次.
    其他的和rdd类似

 */
