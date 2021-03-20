package com.atguigu.spark.day07

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.expressions.{Aggregator, MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, DoubleType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Dataset, Encoder, Encoders, Row, SparkSession, TypedColumn, functions}
import org.junit._

/**
 * Created by VULCAN on 2020/12/5
 *
 *      函数：  UDF ： 一进一出。 输入一行，输出一行(一般一行一列)
 *              UDAF：  多进一出。  输入N行N列 ，输出 一行一列(一般一行一列)
 *                        avg()
 *                        count()
 *              UDTF :  一进多出：  输入一行N列，输出 N行N列
 *                          explode()
 *
 *      SparkSQL兼容HQL，hive中的函数可以直接使用！
 *
 *            提供了自己内置的函数： http://spark.apache.org/docs/latest/sql-ref-functions-builtin.html
 *
 *            用户可以根据Spark的规范定义UDF和UDAF
 *
 *            UDAF:
 *                      spark2.x :   UserDefinedAggregateFunction  弱类型
 *                                    Aggregator  强类型
 *
 *                      spark3.x :   UserDefinedAggregateFunction(过时)
 *                                    Aggregator
 *
 *
 *
 */

// 样例类的属性名必须和 数据的属性名一直
case class  Emp(var name:String ,var salary:Double)
class CustomFunctionTest {

  /*
      DSL中的Aggregator 和 SQL中使用的 Aggregator 的区别：
      case class Data(i: Int)
 *
 *   val customSummer =  new Aggregator[Data, Int, Int] {
 *     def zero: Int = 0
 *     def reduce(b: Int, a: Data): Int = b + a.i
 *     def merge(b1: Int, b2: Int): Int = b1 + b2
 *     def finish(r: Int): Int = r
 *   }.toColumn()
 *
 *   val ds: Dataset[Data] = ...
 *   val aggregated = ds.select(customSummer)


val ds: Dataset[Data]
Aggregator[Data, Int, Int]
 ds.select(customSummer)
    DSL的Aggregator 的IN就是T

 ----------------------------
 Aggregator[Double,(Double,Int),Double ]
   val ds: Dataset[Person] = df.as[Person]

  sparkSession.sql("select myAvg(salary)  from emp ").show()


   */
  // 测试DSL Aggregator
  @Test
  def testUDAF3() : Unit ={

    val df: DataFrame = sparkSession.read.json("input/employees.json")

    import  sparkSession.implicits._

    val ds: Dataset[Emp] = df.as[Emp]

    //自定义函数
    val avg = new MyDSLAvg

    val column: TypedColumn[Emp, Double] = avg.toColumn

    ds.select(column).show()



  }


  // 测试 Aggregator
  @Test
  def testUDAF2() : Unit ={

    val df: DataFrame = sparkSession.read.json("input/employees.json")

    import  sparkSession.implicits._

    val ds: Dataset[Emp] = df.as[Emp]

    ds.createTempView("emp")

    //自定义函数
    val avg = new MyAvg
    // 注册函数，起名
    sparkSession.udf.register("myAvg",functions.udaf(avg) )

    ds.show()

    sparkSession.sql("select myAvg(salary)  from emp ").show()

  }

  // UserDefinedAggregateFunction ，模拟Sum
  @Test
  def testCustomUDAF1() : Unit ={

    val df: DataFrame = sparkSession.read.json("input/employees.json")
    df.createTempView("emp")

    //自定义函数
    val sum = new MySum

    // 注册函数，起名
    sparkSession.udf.register("mySum",sum )

    //使用
    sparkSession.sql("select mySum(salary)  from emp ").show()
  }

  // 自定义函数，将salary自动 增加3000
  @Test
  def testCustomUDF() : Unit ={

    val df: DataFrame = sparkSession.read.json("input/employees.json")

    //创建一个程序中临时使用的表
    df.createTempView("emp")

    //自定义函数
    def addSalary(salary:Double) : Double= salary + 3000

    // 注册函数，起名
    sparkSession.udf.register("myfun1",addSalary _)

    // 使用匿名函数
    sparkSession.udf.register("myfun2", (x:Double) => x + 3000)

    // sql
    sparkSession.sql("select name,salary,myfun2(salary) from emp ").show()


  }

  val sparkSession: SparkSession = SparkSession.builder
    .master("local[*]")
    .appName("sql")
    .getOrCreate()


  // Test之后调用的方法
  @After
  def stop()={

    sparkSession.close()

  }

  //在应用程序Test之前，删除output输出路径
  @Before
  def start(){

    val fileSystem: FileSystem = FileSystem.get(new Configuration())

    val path = new Path("output")

    //判断路径如果存储就删除
    if (fileSystem.exists(path)){
      fileSystem.delete(path,true)
    }

  }

}

class  MyDSLAvg extends  Aggregator[Emp,(Double,Int),Double ] {

  // 初始化缓存区中保存的中间值
  override def zero: (Double, Int) =  (0.0 , 0)

  // 将输入的数据 合并 到缓存区的中间值上
  override def reduce(b: (Double, Int), a: Emp): (Double, Int) = {
    val sum: Double = b._1 + a.salary
    val count: Int = b._2 + 1
    (sum ,count)
  }

  // 将多个缓存区的中间值聚合
  override def merge(b1: (Double, Int), b2: (Double, Int)): (Double, Int) = {

    val sum: Double = b1._1 + b2._1
    val count: Int = b1._2 + b2._2
    (sum , count)
  }

  // 返回结果  reduction:最后合并完的中间值
  override def finish(reduction: (Double, Int)): Double = reduction._1 / reduction._2

  // 缓存区中间值的Encoder类型
  override def bufferEncoder: Encoder[(Double, Int)] = Encoders.tuple[Double,Int](Encoders.scalaDouble,Encoders.scalaInt)

  // 最终输出的结果的Encoder类型
  override def outputEncoder: Encoder[Double] = Encoders.scalaDouble
}

/*
      模拟avg
        [-IN, BUF, OUT]:
            IN:输入类型
            BUF： 保存中间值的类型，缓冲区中保存的值的类型
                      avg = sum / count
                      （sum,count） 活 自定义Bean(sum,count)

            OUT：  输出类型
 */
class  MyAvg extends  Aggregator[Double,(Double,Int),Double ] {

  // 初始化缓存区中保存的中间值
  override def zero: (Double, Int) =  (0.0 , 0)

  // 将输入的数据 合并 到缓存区的中间值上
  override def reduce(b: (Double, Int), a: Double): (Double, Int) = {
    val sum: Double = b._1 + a
    val count: Int = b._2 + 1
    (sum ,count)
  }

  // 将多个缓存区的中间值聚合
  override def merge(b1: (Double, Int), b2: (Double, Int)): (Double, Int) = {

    val sum: Double = b1._1 + b2._1
    val count: Int = b1._2 + b2._2
    (sum , count)
  }

  // 返回结果  reduction:最后合并完的中间值
  override def finish(reduction: (Double, Int)): Double = reduction._1 / reduction._2

  // 缓存区中间值的Encoder类型
  override def bufferEncoder: Encoder[(Double, Int)] = Encoders.tuple[Double,Int](Encoders.scalaDouble,Encoders.scalaInt)

  // 最终输出的结果的Encoder类型
  override def outputEncoder: Encoder[Double] = Encoders.scalaDouble
}


/*
        函数名： mysum
        用：  select  mysum(salary) from emp

      输入 ，输出
 */
class  MySum extends  UserDefinedAggregateFunction {

  // 输入的参数类型   输入的每一行可以是很多列，因此使用StructType
  override def inputSchema: StructType = StructType( StructField("a",DoubleType) :: Nil)

  // 缓冲区的类型
  override def bufferSchema: StructType =  StructType( StructField("a",DoubleType) :: Nil)

  // 返回值的类型
  override def dataType: DataType = DoubleType

  // 是否是确定性函数  输入一致，输出相同的函数，就散确定性函数
  override def deterministic: Boolean = true

  // 初始化缓冲区  buffer:Row,当成数组用
  override def initialize(buffer: MutableAggregationBuffer): Unit = buffer(0) = 0.0

  // 将每一行的输入计算的结果更新到缓存区
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = buffer(0)= buffer.getDouble(0) + input.getDouble(0)

  // 将多个缓存区的值合并  将buffer2合并到buffer1
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    buffer1(0) = buffer2.getDouble(0) + buffer1.getDouble(0)
  }

  // 返回结果
  override def evaluate(buffer: Row): Any = buffer.getDouble(0)
}
