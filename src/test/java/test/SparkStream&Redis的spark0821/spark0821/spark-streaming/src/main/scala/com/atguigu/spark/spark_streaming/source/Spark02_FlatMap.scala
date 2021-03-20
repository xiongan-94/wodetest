package com.atguigu.spark.spark_streaming.source

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Author atguigu
 * Date 2020/12/23 10:17
 */
object Spark02_FlatMap {
    def main(args: Array[String]): Unit = {
        val conf: SparkConf = new SparkConf().setAppName("Spark02_FlatMap").setMaster("local[2]")
        val sc: SparkContext = new SparkContext(conf)
        val list1 = List(3, 50, 70, 6, 1, 20)
        val rdd1: RDD[Int] = sc.parallelize(list1, 2)
        
        /*rdd1
            .flatMap(x => {
                if (x % 2 == 0) List(x * x)
                else Nil
            })
            .collect()
            .foreach(println)*/
        
        rdd1
            .mapPartitions(it => {
                it.filter(_ % 2 == 0)
            })
            .collect()
            .foreach(println)
        
        sc.stop()
        
    }
}
