package com.atguigu.spark.core.app

import com.atguigu.spark.core.beans.UserVisitAction
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by VULCAN on 2020/12/4
 *
 *      控制抽象：
 * def breakable(op: => Unit) {
 * try {
 * op
 * } catch {
 * case ex: BreakControl =>
 * if (ex ne breakException) throw ex
 * }
 * }
 *
 *    基础类，提供对Spark运行环境的封装，希望子类继承
 */
abstract class BaseApp {

  val sparkContext = new SparkContext(new SparkConf().setMaster("local[*]").setAppName("My app"))

  //每个需求结果的输出路径
  val outputPath:String

  //封装了Spark应用运行的一般过程
  def runApp(op: => Unit)={

    //将结果的输出目录进行清理
    start()

    try{
      op
    }catch {
      case e:Exception  => println(e.getMessage)
    }finally {
      sparkContext.stop()
    }

  }

  def start(){

    //删除output目录
    val fileSystem: FileSystem = FileSystem.get(new Configuration())

    val path = new Path(outputPath)

    if (fileSystem.exists(path)){
      fileSystem.delete(path,true)
    }

  }

  //将每行数据封装为Bean
  def getAllDatas():RDD[UserVisitAction]={

    val rdd: RDD[String] = sparkContext.textFile("input/user_visit_action.txt")

    val rdd1: RDD[UserVisitAction] = rdd.map(line => {
      val words: Array[String] = line.split("_")
      UserVisitAction(
        words(0),
        words(1).toLong,
        words(2),
        words(3).toLong,
        words(4),
        words(5),
        words(6).toLong,
        words(7).toLong,
        words(8),
        words(9),
        words(10),
        words(11),
        words(12).toLong
      )
    })
    rdd1

  }

}
