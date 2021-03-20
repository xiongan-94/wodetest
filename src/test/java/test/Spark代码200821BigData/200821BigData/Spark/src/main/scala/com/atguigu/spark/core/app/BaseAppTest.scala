package com.atguigu.spark.core.app

/**
 * Created by VULCAN on 2020/12/4
 */
object BaseAppTest extends  BaseApp {

  override val outputPath: String = "output/BaseAppTest"

  def main(args: Array[String]): Unit = {

    runApp{

      println(sparkContext.textFile("input/a.txt").flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _).
        collect().mkString(","))

    }

  }

}
