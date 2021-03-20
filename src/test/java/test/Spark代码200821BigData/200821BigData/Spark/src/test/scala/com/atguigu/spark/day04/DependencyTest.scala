package com.atguigu.spark.day04

/**
 * Created by VULCAN on 2020/12/1
 *
 * abstract class Dependency[T] extends Serializable {
 *    // 当前RDD依赖的父RDD
 *     def rdd: RDD[T]
 * }
 *
 * Dependency:
 *    作用： ①通过Dependency找父RDD
 *           ②通过Dependency的类型，描述当前RDD和父RDD的依赖关系。通过依赖关系区分是否需要shuffle！
 *                通过依赖关系，确定是否需要shuffle，此时划分阶段！
 *    ShuffleDependency(宽依赖):  产生shuffle！
 *                                    父RDD的一个分区的数据，被多个子RDD的分区依赖！
 * *                                  父RDD的一个分区的数据，需要分区，溢写，拷贝到处理子RDD分区的Task上，产生shuffle！
 *    NarrowDependency(窄依赖): 不产生shuffle！
 *        OneToOneDependency:  子RDD的每一个分区，都单独来自父RDD的一分区！
 *        RangeDependency:  子RDD的每一个分区，来自父RDD的 某个范围的分区！
 *
 *
 *
 *
 */
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.rdd.RDD
import org.apache.spark.{Dependency, SparkConf, SparkContext}
import org.junit._
class DependencyTest {

  @Test
  def test1() : Unit ={

    val list = List(1, 2, 3, 4)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)

    println("---------- rdd ------------")

    val dependencies: Seq[Dependency[_]] = rdd.dependencies

    for (elem <- dependencies) {
      println("dad:"+elem.rdd + " type:" + elem.getClass.getName)
    }

    val rdd1: RDD[Int] = rdd.repartition(4)

    println("---------- rdd 1------------")

    //MapPartitionsRDD
    println(rdd1.getClass.getSimpleName)

    val dependencies1: Seq[Dependency[_]] = rdd1.dependencies

    for (elem <- dependencies1) {
      println("dad:"+elem.rdd + " type:" + elem.getClass.getName)
    }

    rdd1.collect()

    Thread.sleep(100000000)
  }


  @Test
  def testDependency() : Unit ={

    val list = List(1, 2, 3, 4)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)

    println("---------- rdd ------------")

    val dependencies: Seq[Dependency[_]] = rdd.dependencies

    for (elem <- dependencies) {
      println("dad:"+elem.rdd + " type:" + elem.getClass.getName)
    }

    println("---------- rdd 1------------")

    val rdd1: RDD[Int] = rdd.map(x => x + 1)

    val dependencies1: Seq[Dependency[_]] = rdd1.dependencies

    for (elem <- dependencies1) {
      //ParallelCollectionRDD[0] at makeRDD at DependencyTest.scala:33    OneToOneDependency
      println("dad:"+elem.rdd + " type:" + elem.getClass.getName)
    }

    println("---------- rdd2 ------------")

    val rdd2: RDD[Int] = rdd1.filter(x => x > 3)

    val dependencies2: Seq[Dependency[_]] = rdd2.dependencies

    for (elem <- dependencies2) {
      // MapPartitionsRDD[1] at map at DependencyTest.scala:45  OneToOneDependency
      println("dad:"+elem.rdd + " type:" + elem.getClass.getName)
    }



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
