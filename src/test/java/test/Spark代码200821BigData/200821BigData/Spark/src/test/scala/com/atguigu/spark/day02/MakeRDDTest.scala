package com.atguigu.spark.day02

/**
 * Created by VULCAN on 2020/11/28
 */

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.junit._

/*
      所有的单元测试方法的返回值必须是void

      在 Module的 test中编写的代码，相对的目录是当前的module目录！
 */
class MakeRDDTest {


  /*
      textFile 返回的是 HadoopRDD
      分区数： 默认2

          minPartitions: Int = defaultMinPartitions =math.min(defaultParallelism, 2)
              minPartitions 代表最小分区数，不代表最终分区数！
                  最终分区数(参考切片) >= minPartitions

          分区数就散切片数！默认使用TextInputFormat的getSplits(jobConf, minPartitions)
                切片默认按照文件的切片策略切，片大小一般情况都是blockSize!

      分区策略： 切片默认按照文件的切片策略切，片大小一般情况都是blockSize!
   */
  @Test
  def testTextFile() : Unit ={

    val rdd: RDD[String] = sparkContext.textFile("input")

    rdd.saveAsTextFile("output")


  }

  /*
        RDD要么通过转换得到，要么通过new得到！
            new的方式：
                  ①直接new
                  ②调用SparkContext.xx()


        将一个Seq，转换为一个RDD!

        RDD是分区的集合！在创建RDD时需要指定分区数！ 可以手动指定，也可以设置spark.default.parallelism，作为默认的分区数！


        // 从配置中获取用户配置的spark.default.parallelism参数值，如果没有配置，默认使用当前所有可用的核数
        numSlices: Int = defaultParallelism = scheduler.conf.getInt("spark.default.parallelism", totalCores)

   */
  @Test
  def testgetConf() : Unit ={

    println(conf.getInt("spark.default.parallelism", -1))

  }

  @Test
  def testSlice() : Unit ={

    val array: Array[Int] = Array(1, 2, 3, 4, 5)

    //将数组中的元素按照索引切分  [start,end)
    println(array.slice(1, 3).toSeq)

  }


  /*
        ParallelCollectionRDD的分区策略：
            ParallelCollectionRDD在对集合中的元素进行分区时，大致是平均分。如果不能整除，后面的分区会多分！
   */
  @Test
  def testMakeRdd() : Unit ={

    val list = List(1, 2, 3, 4, 5)

    //val rdd1: RDD[Int] = sparkContext.makeRDD(list,2)
    val rdd1: RDD[Int] = sparkContext.makeRDD(list)
    val rdd2: RDD[Int] = sparkContext.parallelize(list)

    rdd1.saveAsTextFile("output")
    //rdd2.saveAsTextFile("output1")


  }

  val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("My app").
    set("spark.default.parallelism","4")

  val sparkContext = new SparkContext(conf)

  @After
  def close() : Unit ={
    sparkContext.stop()
  }
  @Before
  def start() : Unit ={

    //本地文件系统
    val fileSystem: FileSystem = FileSystem.get(new Configuration())

    // 输出目录
    val path = new Path("output")

    if (fileSystem.exists(path)){

      fileSystem.delete(path,true)

    }

    fileSystem.close()

  }

  
 @Test
 def testWordCount() : Unit ={

   sparkContext.textFile("input").flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _).
     saveAsTextFile("output")
     
 }

}
