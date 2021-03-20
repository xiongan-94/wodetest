package com.atguigu.spark.day05

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.junit._

/**
 * Created by VULCAN on 2020/9/7
 *
 * 1.血缘关系
 *
 *
 * 2.依赖关系  给Spark内部用！ DagSchexxxx
 *
 */
class LineageAndDependencyTest {

  @Test
  def testLineage() : Unit ={

    val list = List(1, 2, 3, 4)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)

    val rdd1: RDD[Int] = rdd.map(x => x)

    val rdd2: RDD[(Int, Int)] = rdd1.map(x => (x, 1))

    val rdd3: RDD[(Int, Int)] = rdd2.reduceByKey(_ + _)

    //整个血缘关系   给程序员用的
    println(rdd3.toDebugString)

    rdd3.collect()

  }

  val sparkContext = new SparkContext(new SparkConf().setMaster("local[*]").setAppName("My app"))

  @Before
  def start(){

    //删除output目录
    val fileSystem: FileSystem = FileSystem.get(new Configuration())

    val path = new Path("output")

    if (fileSystem.exists(path)){
      fileSystem.delete(path,true)
    }

  }

  @After
  def stop(){
    sparkContext.stop()
  }

}
