package com.atguigu.spark.spark_streaming.source

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Author atguigu
 * Date 2020/12/23 10:17
 */


object Spark03_Window_2 {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME", "atguigu")
        val conf = new SparkConf().setMaster("local[2]").setAppName("Spark01_Source_Kafka")
        val ssc = new StreamingContext(conf, Seconds(3))
        ssc.checkpoint("hdfs://hadoop162:8020/ck1")
        ssc
            .socketTextStream("hadoop162", 9999)
            .window(Seconds(9), Seconds(6))
            .flatMap(_.split(" "))
            .map((_, 1L))
            .reduceByKey(_ + _)
            .print()
        
        ssc.start()
        ssc.awaitTermination()
        
    }
}

/*
状态:
    1. 从程序启动开始记录状态, 后面所有的聚合都可以读到相关的状态
    
    2. 窗口
 */

