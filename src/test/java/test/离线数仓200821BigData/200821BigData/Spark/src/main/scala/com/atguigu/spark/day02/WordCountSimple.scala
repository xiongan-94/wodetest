package com.atguigu.spark.day02

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}


object WordCountSimple {

  def main(args: Array[String]): Unit = {

    val conf: SparkConf = new SparkConf().setAppName("My app")

    val sparkContext = new SparkContext(conf)

    println(sparkContext.textFile(args(0)).flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _).
      collect().mkString(","))

    //app 运行完毕，可以调用stop终止
    sparkContext.stop()

  }

}
