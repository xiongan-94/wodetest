package com.atguigu.spark.day03

/**
 * Created by VULCAN on 2020/11/30
 *
 *
 *    讲解分区器(Partitoner)
 *
 *    Partitoner是RDD的一个属性！ 可选的！ 只有RDD[K,V]可能有！
 *        val partitioner: Option[Partitioner] = None
 *
 *     作用： 针对RDD[K,V]，将元素按照K进行分区！ 每个Key都会划分到 [0,numPartitions - 1]
 *
 * abstract class Partitioner extends Serializable {
 *     // 分区器要分的总的区数
 *    def numPartitions: Int
 *
 *    // 基于Kay进行分区
 *    def getPartition(key: Any): Int
 * }
 *
 * 默认实现： HashParttioner   RangePartitioner
 *
 *
 * HashParttioner:   按照key的hashCode进行分区！相同key会被分配到同一个区！
 *                    如何判断equals:
 *                          如果比较的对象也是 HashParttioner，只要总的分区数一致，就相等！
 *                          否则就不等！
 *
 *                   很容易导致数据倾斜！
 *
 *
 * RangeParitioner:   将可排序的记录，大致划分到相等的范围！ 范围由对RDD抽样产生！
 *
 *              局限：  RDD中的元素必须可排序！
 *
 *
 */

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.rdd.RDD
import org.apache.spark.{HashPartitioner, Partitioner, RangePartitioner, SparkConf, SparkContext}
import org.junit._

class PartitionerTest {

  @Test
  def testHashPartitioner() : Unit ={

    val p1 = new HashPartitioner(3)
    val p2 = new HashPartitioner(4)
    val p4 = new HashPartitioner(4)
    val p3 = 1

    println(p1 == p2)
    println(p1 == p4)
    println(p2 == p4)
    println(p1 == p3)

    //  对象.方法

  }

  @Test
  def testHashPartitioner2() : Unit ={

    val list = List(1, 2, 3, 4, 5, 6, 7, 8)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)

    val p1 = new HashPartitioner(3)

    // 只有RDD[K,v]可以使用分区器
    val rdd2: RDD[(Int, Int)] = rdd.map(ele => (ele, 1))

    val rdd3: RDD[(Int, Int)] = rdd2.partitionBy(p1)

    rdd3.saveAsTextFile("output")


  }

  @Test
  def testRangePartitioner() : Unit ={

    val list = List(1, 4, 4, 4, 7, 7, 7, 10)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)

    val rdd2: RDD[(Int, Int)] = rdd.map(ele => (ele, 1))

    val p2 = new RangePartitioner[Int, Int](3, rdd2)

    val rdd3: RDD[(Int, Int)] = rdd2.partitionBy(p2)

    rdd3.saveAsTextFile("output")

    Thread.sleep(1000000)


  }

  /*
        RDD中，将男性分到一个区，女性分到一个区，其他的分到一个区
   */
  @Test
  def testCustomPartitioner() : Unit ={

    val list = List((Person("jack", "male"), 1), (Person("jack1", "unkonwn"), 1), (Person("jack2", "male"), 1), (2,1), (3,1), (4,1), ("haha",1))

    val rdd: RDD[(Any, Int)] = sparkContext.makeRDD(list)

    rdd.partitionBy(new MyPartitioner(3)).saveAsTextFile("output")


  }

  val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("My app")
  //.set("spark.default.parallelism","5")

  val sparkContext = new SparkContext(conf)

  // Test之后调用的方法
  @After
  def stop()={
    sparkContext.stop()
  }

  //在应用程序Test之前，删除output输出路径
  @Before
  def start(){

    // 没有自定义core-site.xml，默认FileSystem 是本地文件系统（windows）
    val fileSystem: FileSystem = FileSystem.get(new Configuration())

    val path = new Path("output")

    //判断路径如果存储就删除
    if (fileSystem.exists(path)){
      fileSystem.delete(path,true)
    }

  }

}

class  MyPartitioner(num:Int) extends  Partitioner {

  override def numPartitions: Int = num

  //RDD中，将男性分到一个区，女性分到一个区，其他的分到一个区
  override def getPartition(key: Any): Int = {

    if (!key.isInstanceOf[Person]){
      0
    }else{
      val person: Person = key.asInstanceOf[Person]

      person.sex match {
        case "female" => numPartitions - 1
        case "male" => numPartitions - 2
        case _ => 0
      }
    }

  }
}

case class Person(var name: String,var sex:String)
