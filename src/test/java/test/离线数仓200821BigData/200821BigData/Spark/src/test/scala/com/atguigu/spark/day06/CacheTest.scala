package com.atguigu.spark.day06

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}
import org.junit._

/**
 * Created by VULCAN on 2020/12/4
 *
 *    Cache（缓存）:  缓存！ 缓冲的存储！
 *                      最初用在数据库查询中！
 *                      目的：  减少对重复元素的查询！加快查询效率！
 *
 *                                缓存为了保证效率，多使用内存作为存储介质！
 *
 *                                优秀的缓存框架：  redis,memcache,megongdb
 *
 *          在Spark中，缓存为了存储一些多个算子共用的RDD！
 *
 *              缓存不会改变血缘关系！因为缓存的不可靠性，在缓存丢失时，需要根据血缘关系重建RDD！
 *
 *
 *
 *  缓存的存储级别：
 * val NONE = new StorageLevel(false, false, false, false) ： 不用缓存
 * val DISK_ONLY = new StorageLevel(true, false, false, false) : 仅仅将RDD缓存到磁盘
 * val DISK_ONLY_2 = new StorageLevel(true, false, false, false, 2) : 仅仅将RDD缓存到磁盘,缓存两个副本
 * val MEMORY_ONLY = new StorageLevel(false, true, false, true) :  仅仅将RDD缓存到内存
 * val MEMORY_ONLY_2 = new StorageLevel(false, true, false, true, 2)
 * val MEMORY_ONLY_SER = new StorageLevel(false, true, false, false) ： 将RDD缓存到内存，以序列化数组的形式存入内存！ 更节省内存！带来了cpu的额外开销！
 * val MEMORY_ONLY_SER_2 = new StorageLevel(false, true, false, false, 2)
 * val MEMORY_AND_DISK = new StorageLevel(true, true, false, true)
 * val MEMORY_AND_DISK_2 = new StorageLevel(true, true, false, true, 2)
 * val MEMORY_AND_DISK_SER = new StorageLevel(true, true, false, false)
 * val MEMORY_AND_DISK_SER_2 = new StorageLevel(true, true, false, false, 2)
 * val OFF_HEAP = new StorageLevel(true, true, true, false, 1) ： 将RDD缓存到堆外内存(OS直接管理的内存)！
 *
 */
class CacheTest {

  @Test
  def testCache() : Unit ={

    val list = List(1, 2, 3, 4)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)

    val rdd1: RDD[Int] = rdd.map(x => {
      println("map")
      x
    })

    println("----------------cache之前-----------------")

    println(rdd1.toDebugString)

    //将rdd1缓存   cache()  ====  persist() ===== persist(StorageLevel.MEMORY_ONLY)
    //rdd1.cache()

    //指定缓存级别为 内存和磁盘一起使用！内存不够溢写到磁盘！
    rdd1.persist(StorageLevel.MEMORY_AND_DISK)


    println("----------------cache之后-----------------")

    println(rdd1.toDebugString)

    rdd1.collect()

    println("---------------------")

    rdd1.saveAsTextFile("output")

    Thread.sleep(100000000)

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
