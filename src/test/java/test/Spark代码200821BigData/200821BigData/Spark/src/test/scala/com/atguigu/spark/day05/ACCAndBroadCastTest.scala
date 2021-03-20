package com.atguigu.spark.day05

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.util.AccumulatorV2
import org.apache.spark.{SparkConf, SparkContext}
import org.junit.{After, Before, Test}

import scala.collection.mutable

/**
 * Created by VULCAN on 2020/12/2
 *
 *      shuffle：  ①数据被重新分区和分布
 *                    且
 *                 ②跨机器和executor传输数据
 *
 *    accumulator ：    是一个仅仅支持累积的变量！在程序中高效地并行累加！
 *
 *              Spark只提供了数值类型的累加器：
 *                    SparkContext.longAccumulator() ： 获取long类型的累加器
 *                    SparkContext.doubleAccumulator() ： 获取double类型的累加器
 *
 *           累加器的方法：
 *              add() :  累加! 在task端累加，task不能调用value()
 *              value() :  在Driver端获取 累加器的值！
 *
 *
 *           如何自定义:  实现AccumulatorV2
 *              必须实现的方法：
 *                  reset() :   将累加器重置归0
 *                  add() :  累加
 *                  merge() : 合并累加器
 *
 *        AccumulatorV2[IN, OUT] :  IN 代表输入，add(e)累加的e的类型！
 *                                  OUT： 输出类型， value()返回值类型
 *
 *
 *
 *             累加器的执行过程：
 *                    a) 在Driver端 new
 *                    b)  将Driver端的累加器 先 copy() 返回一个新的累加器
 *                            reset()，将新的累加器重置归0
 *                            isZero() ,判断归0是否成功，如果为true，就继续，false就抛异常！
 *
 *                    c) 每个Task都会执行b步骤，每个task都会序列化发送一个累加器！
 *
 *
 *     ---------------------------------------------------------
 *
 *     广播变量：  在executor中分享一个大的只读的变量。
 *                    作用：  将多个Task需要的共同的只读大变量，以广播的方式发送到每个机器，而不是将副本传输给每一个Task!
 *
 *                    创建： var bc=SparkContext.broadcast(v)
 *
 *                     如何获取广播的变量v:  bc.value()
 *
 *                     不建议在广播变量后，再修改它的值！
 *
 *
 *
 */

class  MyIntAcc extends  AccumulatorV2[Int,Int] {

  //提供属性，保存累加的值
  private var result : Int =10

  // 判断累加器是否是初始值状态
  override def isZero: Boolean = result ==10

  // 复制累加器
  override def copy(): AccumulatorV2[Int, Int] = new MyIntAcc

  // 重置归0
  override def reset(): Unit = result=10

  // 累加
  override def add(v: Int): Unit = result += v

  // 合并累加器  将other的值合并到当前累加器的值
  override def merge(other: AccumulatorV2[Int, Int]): Unit =  result += other.value

  // 获取累加的结果
  override def value: Int = result
}

/*    输入：String
      hi,
      hello

      输出： Map[String,Int]
                所有当前分区单词的统计结果
 */
class  WCAcc extends   AccumulatorV2[String,mutable.Map[String,Int]] {

  //提供属性累加单词
  private val result: mutable.Map[String, Int] = mutable.Map[String, Int]()

  override def isZero: Boolean = result.isEmpty

  override def copy(): AccumulatorV2[String, mutable.Map[String, Int]] = new WCAcc

  override def reset(): Unit = result.clear()

  override def add(v: String): Unit = {
    result.put(v,result.getOrElse(v,0) + 1)
  }

  // 将other的数据，合并到当前累加器上
  override def merge(other: AccumulatorV2[String, mutable.Map[String, Int]]): Unit = {

    val toMergeMap: mutable.Map[String, Int] = other.value

    for ((key,value) <- toMergeMap) {

      result.put(key,result.getOrElse(key,0) + value)

    }

  }

  override def value: mutable.Map[String, Int] = result
}

class ACCAndBroadCastTest {


  @Test
  def testBroadCast() : Unit ={

    val list = List(1, 2, 3, 4,-2,-3)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)

    // 准备一个超大集合
    val range = Range(1, 100000, 1)

    // 将range广播
    val bc: Broadcast[Range] = sparkContext.broadcast(range)

    // 将在range中的元素从RDD中过滤出来
    // 将闭包变量，序列化，发送给每一个Task
    val rdd2: RDD[Int] = rdd.filter(ele => bc.value.contains(ele))

    rdd2.collect()


  }


  //  累加器的使用场景： 计数和求和   用累加器实现wordcount
  @Test
  def testAcc2() : Unit ={

    val rdd: RDD[String] = sparkContext.textFile("input/a.txt")

    // 单词
    val rdd1: RDD[String] = rdd.flatMap(line => line.split(" "))

    val acc = new WCAcc

    sparkContext.register(acc,"wc")

    // 累加器效率高 ： 避免了累加的shuffle！
    rdd1.foreach(word => acc.add(word))

    println(acc.value)


  }

  @Test
  def testAcc1() : Unit ={

    val list = List(1, 2, 3, 4)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)

    //使用累加器累加  Driver
    val acc = new MyIntAcc

    // 向spark注册，当前对象是累加器
    sparkContext.register(acc,"sumResult")

    // 将累加器当作一个变量，在算子中使用
    rdd.foreach( x => acc.add(x) )

    println(acc.value)

    Thread.sleep(10000000)


  }


  /*

   */
  @Test
  def test1() : Unit ={

    val list = List(1, 2, 3, 4)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)

    // 在Driver端定义
    var sum:Int =0

    //  sum是闭包变量，序列化后，复制到 executor上，在executor进行运算！ 运算后的结果没有返回到Driver！
    rdd.foreach( x => sum += x)

    // 0  如何解决： 使用累加器
    println(sum)

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
