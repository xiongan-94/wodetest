package com.atguigu.spark.spark_streaming.sink

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Author atguigu
 * Date 2020/12/23 14:19
 */
object Spark01_Sink_File {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME", "atguigu")
        val conf = new SparkConf().setMaster("local[2]").setAppName("Spark01_Source_Kafka")
        val ssc = new StreamingContext(conf, Seconds(3))
        
        ssc
            .socketTextStream("hadoop162", 9999)
            .flatMap(_.split(" "))
            .map((_, 1L))
            .reduceByKey(_ + _)
            .foreachRDD(rdd => {
                
                println("xxxx")
                
                /*rdd.foreach(x => {
                
                })*/
                /*rdd.foreachPartition(it => {
                    // 先建立到mysql的连接  一个分区一个连接
                    it.foreach(e => {
                        // 写出去
                    })
                    // 关闭连接
                })*/
                
                // driver
               // val arr: Array[(String, Long)] = rdd.collect()
                // 先建立到mysql的连接
            })
        
        ssc.start()
        ssc.awaitTermination()
    }
}
/*
transform
    转换
foreachRDD
    行动


 */