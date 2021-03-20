package com.atguigu.spark.day04

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.junit._

/**
 * Created by VULCAN on 2020/9/5
 *
 *    本章讲解 RDD[K-V] 常用的算子
 *
 *          select  xx, concat(xx)        ---- map
 *          from xx  join  xxx           ---- join
 *          on xxx
 *          where xxx                     ------ filter
 *          group by xxx                  ------- reduceByKey，distinct
 *          having xx                     ------ filter
 *          order by xxx                  ------  sortBy , sortByKey
 *          limit xxx                     ------  take
 *
 *
 *
 *
 */
case class  Person(name:String,age:Int)

class RDDKeyValueOperationTest {

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
        如何调用PairRDDFunctions中的方法？

              类型转换：  ①特质动态混入
                          ②隐式转换
                               隐式转换函数
                                    位置：  当前目标类的类文件;  目标类的父类或trait的类文件; 目标类的伴生对象或父类伴生对象的类文件; 包对象;


   implicit def rddToPairRDDFunctions[K, V](rdd: RDD[(K, V)])
    (implicit kt: ClassTag[K], vt: ClassTag[V], ord: Ordering[K] = null): PairRDDFunctions[K, V] = {
    new PairRDDFunctions(rdd)

    为一要求，当前RDD是RDD[(K, V)]
  }
   */
  @Test
  def testHowToUsePairRDDFunctions() : Unit ={

    val list = List(1, 2, 3, 4)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)

    // 希望调用 PairRDDFunctions的方法，首先需要先将 RDD 转换为 PairRDDFunctions类型才可以
    val rdd1: RDD[(Int, Int)] = rdd.map(ele => (ele, 1))

    //rdd1.reduc
      
  }

  /*
      partitionBy算子
   */
  @Test
  def testPartitionBy() : Unit ={

    val list = List(1, 2, 3, 4)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)
    
  }
  
  /*
      自定义分区器的使用
   */
  @Test
  def testCustomPartioner() : Unit ={
  

  }

  /*
        MapValues算子
            将K-V类型，相同K对应的values，通过函数运算，之后将运算的结果和原先的k组合返回新的RDD

            没有shuffle！
   */
  @Test
  def testMapValues() : Unit ={

    val list = List((1, 1), (2, 1), (3, 1), (4, 1))

    val rdd: RDD[(Int, Int)] = sparkContext.makeRDD(list, 2)

    val rdd1: RDD[(Int, String)] = rdd.mapValues(value => value + "haha")

    rdd1.saveAsTextFile("output")

  }

  /*
      ReduceByKey算子
          将相同key的value，通过函数聚合，将结果和K共同返回，产生新的RDD！
   */
  @Test
  def testReduceByKey() : Unit ={

    val list = List((1, 1), (2, 1), (3, 1), (4, 1))

    val rdd: RDD[(Int, Int)] = sparkContext.makeRDD(list, 2)

    //rdd.reduceByKey()

  }

  /*

    groupByKey算子

    reduceByKey和groupByKey的区别？
        算子名称不同！功能不同！
            reduceByKey，按照key分组后，聚合！

             groupByKey，按照key分组！不聚合！

    CompactBuffer: 数组

   */
  @Test
  def testGroupByKey() : Unit ={

    val list = List((1, 1), (1, 2), (2, 1), (2, 2))

    val rdd: RDD[(Int, Int)] = sparkContext.makeRDD(list, 2)

    rdd.groupByKey().saveAsTextFile("output")


  }

  /*
      aggregateByKey 初体验

    def aggregateByKey[U: ClassTag](zeroValue: U)(seqOp: (U, V) => U,
            seqOp:  在一个分区中，运行，负责将key的values，合并到 U类型的 zeroValue，合并后返回新的U类型的zeroValue

      combOp: (U, U) => U): RDD[(K, U)] = self.withScope {
            combOp:  在多个分区间，将每个分区合并的U类型的zeroValue，进一步合并！
    aggregateByKey(zeroValue, defaultPartitioner(self))(seqOp, combOp)
  }


  手推：
        0号区：  (1, 1), (1, 2), (2, 1)
                  将相同key的value进行分组
               SeqOp
                  (1,{1,2})  =>   (1,3)
                  (2,{1})    =>    (2,1)




        1号区：    (1,3),(2,2),(2, 2)
                    将相同key的value进行分组
                SeqOp
                  (1,{3})  =>  (1,3)
                  (2,{2，2}) =>  (2,4)

-----------------------------------------------------
    combOp:
        0号区:  (2,4)，(2,1)  =>    (2,{1,4})  =>    (2,{5})
        1号区：  (1,3)，(1,3) =>     (1,{3,3})  =>    (1,{6})


   */
  @Test
  def testAggregateByKey() : Unit ={

    val list = List((1, 1), (1, 2), (2, 1), (1,3),(2,2),(2, 2))

    val rdd: RDD[(Int, Int)] = sparkContext.makeRDD(list, 2)

    val rdd1: RDD[(Int, Int)] = rdd.aggregateByKey(0)(_ + _, _ + _)

    rdd1.saveAsTextFile("output")

  }

  /*
      aggregateByKey 练习1 ： 取出每个分区内相同key的最大值然后分区间相加
            zeroValue:  保证zeroValue一定小于每个key的最小的value
                          取Int.MinValue
            seqOp: 分区内相同key的最大值
            combOp:  分区间相加

   */
  @Test
  def testAggregateByKeyExec1() : Unit ={

    val list = List((1, 1), (1, 2), (2, 1), (1,3),(2,2),(2, 2))

    val rdd: RDD[(Int, Int)] = sparkContext.makeRDD(list, 2)

    val rdd1: RDD[(Int, Int)] = rdd.aggregateByKey(Int.MinValue)((zeroValue, value) => zeroValue.max(value), (zeroValue1, zeroValue2) => zeroValue1 + zeroValue2)

    rdd1.saveAsTextFile("output")
  }

  /*
      aggregateByKey 练习2 ：分区内同时求最大和最小，分区间合并
           zeroValue:   （a，b）
                    保证zeroValue一定小于每个key的最小的value
                          取Int.MinValue
            seqOp: 分区内相同key的最大值
            combOp:  分区间相加
   */
  @Test
  def testAggregateByKeyExec2() : Unit ={

    val list = List((1, 1), (1, 2), (2, 1), (1,3),(2,2),(2, 2))

    val rdd: RDD[(Int, Int)] = sparkContext.makeRDD(list, 2)

  /*  rdd.aggregateByKey((Int.MinValue,Int.MaxValue)){
      case ((minValue:Int, maxValue:Int), value:Int) => {(minValue.max(value), maxValue.min(value))} ,
      case ((maxValue1:Int, minValue1:Int), (maxValue2:Int, minValue2:Int)) => {
        (maxValue1 + maxValue2, minValue1 + minValue2)
      }
    }*/

    val rdd1: RDD[(Int, (Int, Int))] = rdd.aggregateByKey((Int.MinValue, Int.MaxValue))(
      (zeroValue: (Int, Int), value: Int) => {
        (zeroValue._1.max(value), zeroValue._2.min(value))
      },

      (zeroValue1, zeroValue2) => {
        (zeroValue1._1 + zeroValue2._1, zeroValue1._2 + zeroValue2._2)
      }
    )

    rdd1.saveAsTextFile("output")



  }

  /*
      aggregateByKey 练习3： 求每个key对应的平均值

            平均值 =  总值 /  个数

             zeroValue:   （sum，count）

            seqOp: 将key的每个value和zeroValue合并
            combOp:  分区间相加

   */
  @Test
  def testAggregateByKeyExec3() : Unit ={

    val list = List((1, 1), (1, 2), (2, 1), (1,3),(2,2),(2, 2))

    val rdd: RDD[(Int, Int)] = sparkContext.makeRDD(list, 2)

    //  RDD[(Int, (Double, Int))] =  RDD[(Key, (Sum, Count))]
    val rdd1: RDD[(Int, (Double, Int))] = rdd.aggregateByKey((0.0, 0))(
      (zeroValue: (Double, Int), value: Int) => {
        (zeroValue._1 + value, zeroValue._2 + 1)
      },

      (zeroValue1, zeroValue2) => {
        (zeroValue1._1 + zeroValue2._1, zeroValue1._2 + zeroValue2._2)
      }
    )

    val rdd2: RDD[(Int, Double)] = rdd1.mapValues {
      case (sum, count) => sum / count
    }

    rdd2.saveAsTextFile("output")

  }

  /*
      foldByKey算子： 是AggregateByKey的简化版
            如果AggregateByKey 的 seqOp 和 combOp 是一样的！
                且 zeroValue也是V类型，就可以简写为foldByKey
   */
  @Test
  def testFoldByKey() : Unit ={

    val list = List((1, 1), (1, 2), (2, 1), (1,3),(2,2),(2, 2))

    val rdd: RDD[(Int, Int)] = sparkContext.makeRDD(list, 2)

    val rdd1: RDD[(Int, Int)] = rdd.aggregateByKey(0)(_ + _, _ + _)

    // 等价
    val rdd2: RDD[(Int, Int)] = rdd.foldByKey(0)(_ + _)

    rdd1.saveAsTextFile("output")
    rdd2.saveAsTextFile("output2")

  }

  /*
      CombineByKey算子： 本质和aggregateByKey类似！
              区别：  aggregateByKey 是  CombineByKey的简化版！
                        如果CombineByKey的createCombiner ：  将第一个value返回作为zeroValue，简化为 aggregateByKey

     def combineByKey[C](
      createCombiner: V => C,    将相同key的第一个value传入，运算后得到zeroValue，之后第一个value将不再累加到zeroValue上！
      mergeValue: (C, V) => C,
      mergeCombiners: (C, C) => C): RDD[(K, C)]
   */
  @Test
  def testCombineByKey() : Unit ={

    val list = List((1, 1), (1, 2), (2, 1), (1,3),(2,2),(2, 2))

    val rdd: RDD[(Int, Int)] = sparkContext.makeRDD(list, 2)

    val rdd1: RDD[(Int, Int)] = rdd.combineByKey(
      (v: Int) => v + 10,
      (zeroValue: Int, value: Int) => zeroValue + value,
      (zeroValue1: Int, zeroValue2: Int) => zeroValue1 + zeroValue2
    )

    rdd1.saveAsTextFile("output")

  }


  /*
      CombineByKey算子练习：
          将数据List(("a", 88), ("b", 95), ("a", 91), ("b", 93), ("a", 95), ("b", 98))求每个key的平均值
   */
  @Test
  def testCombineByKeyExec() : Unit ={

    val list = List(("a", 88), ("b", 95), ("a", 91), ("b", 93), ("a", 95), ("b", 98))

    val rdd: RDD[(String, Int)] = sparkContext.makeRDD(list, 2)

    val rdd1: RDD[(String, (Double, Int))] = rdd.combineByKey(
      (value: Int) => (value.toDouble, 1)
      ,
      (zeroValue: (Double, Int), value: Int) => {
        (zeroValue._1 + value, zeroValue._2 + 1)
      },
      (zeroValue1: (Double, Int), zeroValue2: (Double, Int)) => {
        (zeroValue1._1 + zeroValue2._1, zeroValue1._2 + zeroValue2._2)
      }
    )

    val rdd2: RDD[(String, Double)] = rdd1.mapValues {
      case (sum, count) => sum / count
    }

    rdd2.saveAsTextFile("output")

  }

  /*
      ReduceByKey,CombineByKey,AggretageByKey,FoldByKey的联系和转换

          以上四个函数，都本质上调用combineByKeyWithClassTag
              都会在mapSideCombine (在mapTask端进行聚合，类似Hadoop中combiner的作用)

              Hadoop:
                      Reducer:        在reduce阶段运行，shuffle后运行！
                      Combiner:  也是Reducer。    在shufle中运行！
                                      MapTask端的shuffle
                                          Combiner会将数据提前在map端聚合，减少shuffle传输的数据量！
                                      ReduceTask端的shuffle

      需求：  求相同key的values的总和
   */
  @Test
  def test4OperationTransform() : Unit ={

    val list = List(("a", 87), ("b", 95), ("a", 91), ("b", 93), ("a", 95), ("b", 98))

    val rdd: RDD[(String, Int)] = sparkContext.makeRDD(list, 2)

    //CombineByKey
    val rdd1: RDD[(String, Int)] = rdd.combineByKey(
      value => value,
      (zeroValue: Int, value: Int) => zeroValue + value,
      (zeroValue1: Int, zeroValue2: Int) => zeroValue1 + zeroValue2
    )

    /*
        AggretageByKey   :  将CombineByKey  createCombiner函数的返回值作为 zeroValue

 def combineByKey[C](
      createCombiner: V => C,
      mergeValue: (C, V) => C,
      mergeCombiners: (C, C) => C): RDD[(K, C)] = self.withScope {
    combineByKeyWithClassTag(createCombiner, mergeValue, mergeCombiners)(null)
  }

  aggregateByKey(createCombiner() - value) ( mergeValue,mergeCombiners )

    def aggregateByKey[U: ClassTag](zeroValue: U)(seqOp: (U, V) => U,
      combOp: (U, U) => U): RDD[(K, U)] = self.withScope {
    aggregateByKey(zeroValue, defaultPartitioner(self))(seqOp, combOp)
  }
     */

    val rdd2: RDD[(String, Int)] = rdd.aggregateByKey(0)(_ + _, _ + _)

    /*
        如果zeroValue 和 value是同一类型且  seqOp = combOp
        简化为foldByKey

     */

    val rdd3: RDD[(String, Int)] = rdd.foldByKey(0)(_ + _)

    /*
        如果 zeroValue 和  value是同一类型且 zeroValue=0
        此时可以简化为 reduceByKey

     */
    val rdd4: RDD[(String, Int)] = rdd.reduceByKey(_ + _)


  }

  /*
      SortByKey算子：
      SortByKey算子在哪？   OrderedRDDFunctions
      如何调用OrderedRDDFunctions中的方法？  通过隐式转换
    implicit def rddToOrderedRDDFunctions[K : Ordering : ClassTag, V: ClassTag](rdd: RDD[(K, V)])
    : OrderedRDDFunctions[K, V, (K, V)] = {
    new OrderedRDDFunctions[K, V, (K, V)](rdd)
  }

   */
  @Test
  def testSortByKey() : Unit ={

    val list = List(("a", 88), ("b", 95), ("a", 91), ("b", 93), ("a", 95), ("b", 98))

    val rdd: RDD[(String, Int)] = sparkContext.makeRDD(list, 2)

    rdd.sortByKey(false,1).saveAsTextFile("output")

  }

  /*
      SortByKey算子：排序自定义Bean
            将要排序的字段，封装为key


              private val ordering = implicitly[Ordering[K]]

              new ShuffledRDD[K, V, V](self, part)
      .setKeyOrdering(if (ascending) ordering else ordering.reverse)
   */
  @Test
  def testSortByKey2() : Unit ={

    val list = List(Emp2("jack", 20), Emp2("jack", 21), Emp2("marry", 21), Emp2("tom", 23))

    val rdd: RDD[Emp2] = sparkContext.makeRDD(list, 2)

    //提供一个比较器， 先按名称升序，名称相同，按照年龄降序，将比较器放入冥界
    implicit val myOrdering = new Ordering[Emp2]{

      override def compare(x: Emp2, y: Emp2): Int = {
        var result: Int = x.name.compareTo(y.name)

        if (result == 0){
          result = -x.age.compareTo(y.age)
        }
        result
      }
    }


    val rdd1: RDD[(Emp2, Int)] = rdd.map(emp => (emp, 1))

    rdd1.sortByKey(false,1).saveAsTextFile("output")


  }

  //隐式变量
 implicit var i : Int = 10


  /*
        冥界(隐式)召唤
   */
  @Test
  def testImplicitly() : Unit ={

    //当前希望从当前方法的作用域中，召唤一个指定类型的变量
    val j: Int = implicitly[Int]

    println(j)


  }

  /*
      Join操作的算子    都会产生shuffle
          Join:
          leftJoin：
          rightjoin：
          full join：
          corgroup:
   */
  @Test
  def testJoin() : Unit ={

    val list1 = List(("a", 88), ("b", 95),("c",20),("b",93))
    val list2 = List(("a", 2), ("b", 2),("d",30),("a",30))

    val rdd1: RDD[(String, Int)] = sparkContext.makeRDD(list1, 2)
    val rdd2: RDD[(String, Int)] = sparkContext.makeRDD(list2, 2)

   rdd1.join(rdd2).saveAsTextFile("output")
   // rdd1.leftOuterJoin(rdd2,1).saveAsTextFile("output")
    //rdd1.rightOuterJoin(rdd2,1).saveAsTextFile("output")
    //rdd1.fullOuterJoin(rdd2,1).saveAsTextFile("output")

    rdd1.cogroup(rdd2).saveAsTextFile("output")
  }


  
  
}

case class Emp2(name:String,age:Int)




