package com.atguigu.spark.day06

import com.atguigu.spark.day05.User2
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}
import org.junit._

/**
 * Created by VULCAN on 2020/12/4
 *
 *    checkpiont :
 *            作用：  ①将共用的RDD进行缓存！
 *                    ②将Job的计算状态写入到CK目录，允许Driver在失败时，基于CK目录之前保存的状态恢复重启！在sparkStreaming体会！
 *            解决cache容易失效的问题！ checkpiont将RDD存入文件系统(SparkContext#setCheckpointDir)中！
 *
 *            强烈建议先将RDD缓存到内存，之后再调用checkpoint，否则RDD会被重复计算！
 *
 *            特点：血缘关系会被切断！
 */
class CheckPointTest {

  @Test
  def testCheckPoint() : Unit ={

    val list = List(1, 2, 3, 4)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)

    val rdd1: RDD[Int] = rdd.map(x => {
      println("map")
      x
    })

    println("----------------ck之前-----------------")

    println(rdd1.toDebugString)

    //设置ck存储的目录,如果在集群中允许，必须是HDFS上的路径
    sparkContext.setCheckpointDir("ck")

    // cache和checkpoint的顺序无所谓，只要保证在行动算子之前即可
    rdd1.checkpoint()

    rdd1.cache()




    // 将RDD写入到 文件系统，在第一次提交行动算子时被触发
    rdd1.collect()

    println("----------------ck之后-----------------")

    println(rdd1.toDebugString)

    println("---------------------")

    rdd1.saveAsTextFile("output")

    Thread.sleep(100000000)

  }

  // 标识自定义的类也使用kryo进行序列化
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
