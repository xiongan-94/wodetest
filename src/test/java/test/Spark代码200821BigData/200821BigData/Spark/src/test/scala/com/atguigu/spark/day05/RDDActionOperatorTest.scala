package com.atguigu.spark.day05

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.junit._

/**
 * Created by VULCAN on 2020/9/5
 *
 *    本章介绍行动算子
 *
 */
class RDDActionOperatorTest {

  /*

   */
  @Test
  def testForeach() : Unit ={

    val list = List(11, 2, 3, 4,5,5,5,5)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 3)

    // 算子（分布式运算）
   // rdd.foreach(ele => println(Thread.currentThread().getName+":"+ele))

    // 先收集数据到Driver端，构建数组之后在Driver的main线程中遍历
    rdd.collect().foreach(e => println(Thread.currentThread().getName+":"+e))

    //和 mapPartition一个道理，批处理，一个分区处理一次！  使用foreachPartition向数据库写数据！
    //rdd.foreachPartition()

  }

  /*
     特例
          new RangerPartiitoner，此时由于需要抽样产产生边界数组，会多提交一个Job
  */
  @Test
  def testParticularCase() : Unit ={

    val list = List(1, 2, 3, 4)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)

    Thread.sleep(1000000000)

  }


  /*
      常见行动算子

2.reduce: 将RDD中的元素通过reduce函数计算，得到合并后的值！
              函数必须是可交换的！  +,*,最大，最小

3.collect: 慎用！  当RDD中的元素少时，可以使用！ 所有的数据都会加载到Driver端的内存中！容易OOM！
4.count
5.first
6.take :慎用！  当RDD中的元素少时，可以使用！ 所有的数据都会加载到Driver端的内存中！容易OOM！
7.takeOrdered : 慎用！  当RDD中的元素少时，可以使用！ 所有的数据都会加载到Driver端的内存中！容易OOM！
8.aggregate :  zeroValue会多参与一次运算！
9.fold :    zeroValue会多参与一次运算！
10.countByKey
11.countByValue
12.save相关
13.foreach


   */
  @Test
  def testActionOperation() : Unit ={

    val list = List(11, 2, 3, 4,5,5,5,5)
    val list2 = List((11,1), (2,1),(5,1),(5,2),(5,3))

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 3)
    val rdd2: RDD[(Int, Int)] = sparkContext.makeRDD(list2, 3)

   /* println(rdd.reduce(_ + _))
    rdd.collect()*/
   /*println(rdd.count())
    println(rdd.first())
    println(rdd.take(3).mkString(","))
    println(rdd.takeOrdered(3).mkString(","))*/

    // zeroValue不仅在分区内参与运算，还在分区间参与运算！
    /*println(rdd.aggregate(10)(_ + _, _ + _))
    println(rdd.fold(10)(_ + _))

    println(rdd2.countByKey())
    println(rdd.countByValue())*/

    // 20 , -10 ,-8
    println(rdd.reduce(_ - _))

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
