package com.atguigu.spark.day05

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.junit._

/**
 *    java.io.Serializable 1.1 就有！
 *          设计时并没有考虑大数据的应用场景！ 接口保存的类的信息过多！
 *
 *          在大数据领域一般只关心数据，不关心数据结构！
 *
 */

class MyUser3(){

  // 方法(类的成员)，函数
  def fun2(x:Int):Boolean={
    x > 2
  }

  def fun1(rdd : RDD[Int]):RDD[Int] ={

    // 函数
    def fun2(x:Int):Boolean={
      x > 2
    }

    //val result: RDD[Int] = rdd.filter(fun2)
    val result: RDD[Int] = rdd.filter(x => x>2)
    result
  }
}

class RDDSerDeTest {

  /*
  演示和方法相关的序列化
      org.apache.spark.SparkException: Task not serializable

      方法依附于类，作为类的成员！如果算子中使用了某个方法，那么这个方法所在的类必须实现序列化！
            使用函数替换方法！ 也可以使用匿名函数！

*/
  @Test
  def test4() : Unit ={
    val list = List(1, 2, 3, 4)
    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)
    val myUser = new MyUser3
    myUser.fun1(rdd).collect()

  }


  /*
     演示和属性相关的序列化
        org.apache.spark.SparkException: Task not serializable
          如果算子中使用的闭包变量是某个对象的成员，这个对象所在的类也必须实现序列化！
              使用的闭包变量必须依附于对象存在！

          可以在算子中，声明一个局部变量，将引用的成员变量的值赋值给局部变量！
  */
  @Test
  def test3() : Unit ={
    val list = List(1, 2, 3, 4)
    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)
    val myUser = new MyUser2(2)
    val rdd2: RDD[Int] = myUser.fun1(rdd)
    rdd2.collect()

  }


  /*
      体会kryo
            不使用kryo:  658.0 B
            Spark自带的这些类默认已经支持使用Kryo! 用户自定义的类，需要自己指定！

            使用kryo:  302.0 B
   */
  @Test
  def testKryo() : Unit ={

   val list = List(User2(), User2(), User2(), User2(), User2(), User2(), User2(), User2(), User2(), User2(), User2(), User2(), User2(), User2(), User2(), User2()
      , User2(), User2(), User2(), User2(), User2(), User2(), User2(), User2(),
      User2(), User2(), User2(), User2(), User2(), User2(), User2(), User2(), User2(), User2(), User2(), User2(), User2(), User2(), User2(), User2())

    val rdd: RDD[User2] = sparkContext.makeRDD(list, 2)

    // 使用带shuffle的算子测试序列化
    val rdd1: RDD[(User2, Iterable[User2])] = rdd.groupBy(x => x)

    rdd1.collect()

    Thread.sleep(100000000)
  }





  /*
        org.apache.spark.SparkException: Task not serializable

            解决： 保证闭包使用的变量是可以序列化的！
                      ① extends Serilizable
                      ② 使用样例类(自动实现Serilizable)
   */
  @Test
  def test2() : Unit ={

    val list = List(1, 2, 3, 4)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)

    // 在Driver端声明
    val myUser = new MyUser()

    // 在某个executor上运算
    rdd.foreach( x => println(myUser.toString + x))

  }

  /*

   */
  @Test
  def test1() : Unit ={

    val list = List(1, 2, 3, 4)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)

    var sum:Int =0

    rdd.foreach( x => sum += x)

    println(sum)

  }

  // 标识自定义的类也使用kryo进行序列化
  val sparkContext = new SparkContext(new SparkConf().setMaster("local[*]").setAppName("My app").registerKryoClasses(Array(
    classOf[User2]
  )))

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

//class MyUser() extends  Serializable

case class  MyUser()

// 对应 test3
class  MyUser2(age : Int ){
  //过滤 超过 age属性的RDD中的元素
  def fun1(rdd : RDD[Int]):RDD[Int] ={

    //声明局部变量接收 age的值
    var myage=age

    val result: RDD[Int] = rdd.filter(ele => ele > myage)
    result
  }
}
case class User2(age:Int=10,name:String="fhaowiehfaoiwhfoiua;whfeiawofh oi;aweh foiawhfoikjauwhfoi;auwhfeoiu;awehfaoiu;wehfaoiewu;fh")