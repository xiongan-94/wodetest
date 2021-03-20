package com.atguigu.spark.day05

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.junit.{After, Before}

/**
 * Created by VULCAN on 2020/9/4
 *    本章讲解 两个RDD参与运算的算子，常有的有以下算子：
 *
        zipWithIndex
        zipPartitions
        zip
        intersection
        union
        subtract
        cartesian
 */

import org.junit._

class DoubleValueRDDOperationTest {

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

  /*
    zipPartitions算子：
   */
  @Test
  def testzipPartitionsInSpark() : Unit ={
    val list1 = List(1, 2, 3, 4,5,6,7,8)
    val list2 = List("a", "b", "c","d","e","f")

    val rdd1: RDD[Int] = sparkContext.makeRDD(list1, 2)
    val rdd2: RDD[String] = sparkContext.makeRDD(list2, 2)

    //借助zipAll实现全拉链
    rdd1.zipPartitions(rdd2)((iter1,iter2) => iter1.zipAll(iter2,0,"z")).saveAsTextFile("output")

  }

  /*
        Zip算子
          如果两个RDD数据类型不一致怎么办？
                不影响
          如果两个RDD数据分区不一致怎么办？
               Can't zip RDDs with unequal numbers of partitions:  无法拉链
          如果两个RDD分区数据数量不一致怎么办？
               Can only zip RDDs with same number of elements in each partition : 只能在每个分区数据量相同时才能拉链
   */
  @Test
  def testZip() : Unit ={

    val list1 = List(1, 2, 3, 4,5,6,7,8)
    val list2 = List("a", "b", "c","d","e","f")

    val rdd1: RDD[Int] = sparkContext.makeRDD(list1, 2)
    val rdd2: RDD[String] = sparkContext.makeRDD(list2, 2)

    rdd1.zip(rdd2).saveAsTextFile("output")

  }

  /*
        zipWithIndex算子
   */
  @Test
  def testZipWithIndexInSpark() : Unit ={

    val list1 = List(1, 2, 3, 4,5,6)

    val rdd1: RDD[Int] = sparkContext.makeRDD(list1, 2)

    rdd1.zipWithIndex().saveAsTextFile("output")

  }

  /*
      Scala集合中的zipWithIndex
            集合中的元素和索引拉链：  (元素,索引)
   */
  @Test
  def testZipWithIndex() : Unit ={

    val list = List(1, 2, 3, 4)

    println(list.zipWithIndex)

  }

  /*
     Scala集合中的ZipAll
  */
  @Test
  def testZipPartitions() : Unit ={

    val list1 = List(1, 2, 3, 4,5,6,7)
    val list2 = List("a", "b", "c","d","e","f")

    //println(list1.zip(list2))

    println(list1.zipAll(list2, 0, "None"))


  }


  /*
        cartesian 算子
            没有shuffle!
            结果RDD分 m * n 个区，m,n是两个运算的RDD的分区数！
   */
  @Test
  def testCartesian() : Unit ={

    val list1 = List(1, 2, 3, 4)
    val list2 = List(5, 4, 4,2)

    val rdd1: RDD[Int] = sparkContext.makeRDD(list1, 3)
    val rdd2: RDD[Int] = sparkContext.makeRDD(list2, 2)

    rdd1.cartesian(rdd2).saveAsTextFile("output")

  }

  /*
       subtract算子：   分区调用顺序。 默认使用当前RDD的分区器/分区数
                              如果当前RDD没有分区器，默认使用HashParitioner

                              有shuffle！
            A 差集 B 和 B 差集 A 结果不一样！
   */
  @Test
  def testSubtract() : Unit ={

    val list1 = List(1, 2, 3, 4)
    val list2 = List(5, 4, 4,2)

    val rdd1: RDD[Int] = sparkContext.makeRDD(list1, 3)
    val rdd2: RDD[Int] = sparkContext.makeRDD(list2, 2)

    val rdd3: RDD[Int] = rdd1.subtract(rdd2)

    println(rdd3.partitioner)

      rdd3.saveAsTextFile("output")

  }

  /*
    union算子：  等价于 ++ , 无shuffle，窄依赖！
          a: ｛1，2，3｝
          b:   {2,3,4}
        数学上的并集：  {1,2,3,4}
        数据操作中的并集 : ｛1，2，3，2，3，4｝  当前
   */
  @Test
  def testUnion() : Unit ={

    val list1 = List(1, 2, 3, 4)
    val list2 = List(5, 2, 4,6)

    val rdd1: RDD[Int] = sparkContext.makeRDD(list1, 3)
    val rdd2: RDD[Int] = sparkContext.makeRDD(list2, 2)

    rdd1.union(rdd2).saveAsTextFile("output")
    /*val rdd3: RDD[Int] = rdd1 ++ rdd2

    rdd3.saveAsTextFile("output")*/

  }

  /*
      Intersection算子: 有shuffle

   */
  @Test
  def testIntersection() : Unit ={

    val list1 = List(1, 2, 3, 4)
    val list2 = List(5, 2, 4,6)

    val rdd1: RDD[Int] = sparkContext.makeRDD(list1, 3)
    val rdd2: RDD[Int] = sparkContext.makeRDD(list2, 2)

    rdd1.intersection(rdd2).saveAsTextFile("output")


  }



}
