package com.atguigu.spark.day07

/**
 * Created by VULCAN on 2020/9/22
 *
 *    SparkCore:
 *            SparkContext
 *            RDD,累加器，广播变量
 *
 *     Sparksql:
 *            SparkSession
 *            DF,DS
 */


import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{DataFrame, Dataset, Encoders, Row, SparkSession}
import org.junit._


case class  Person(name:String,age:Int)
//case class  Emp(name:String,salary:Long)

class SparkSQLTest {



  /*
        通过Spark自带的api，先获取一个df，再将df转换为我们想要的模型！

        df -> ds
               import sparkSession.implicits._
              df.as[T]
        ds -> df
              ds.toDF()
        df -> RDD
            DataFrame.rdd
        ds -> RDD
             DataSet.rdd
        RDD -> DF
              import sparkSession.implicits._
            toDF()
        RDD -> DS
          import sparkSession.implicits._
            toDS()

        转换 总结



        每一个SparkSession对象，都有 implicits 对象，是SQLImplicits类型！ 其中有很多隐式转换的方法！
         object implicits extends SQLImplicits

   implicit def rddToDatasetHolder[T : Encoder](rdd: RDD[T]): DatasetHolder[T] = {
    DatasetHolder(_sqlContext.createDataset(rdd))
  }

   */
  @Test
  def testConversion() : Unit ={

    val list = List(Person("jack", 20), Person("jack", 20), Person("jack", 20), Person("jack", 20))

    val rdd: RDD[Person] = sparkSession.sparkContext.makeRDD(list, 2)

    // 静态导入
    import sparkSession.implicits._

    //  rddToDatasetHolder(rdd).toDF()
    val df: DataFrame = rdd.toDF()

    val ds: Dataset[Person] = rdd.toDS()

    // DF 转 DS
    val ds1: Dataset[Person] = df.as[Person]

    // DS 转 DF
    val df1: DataFrame = ds1.toDF()


  }

  /*
          SparkSession.createDataset

           def createDataset[T : Encoder](data: Seq[T]): Dataset[T]
              等价于
           def createDataset[T](data: Seq[T])(implicit a: Encoder[T]): Dataset[T]

            def createDataset[Person](data: Seq[Person])(implicit a: Encoder[Person]): Dataset[Person]

            Encoder: 有用！

                  隐式： 通过导入SparkSession中的一些隐式的方法，自动创建
                             import sparkSession对象.implicits._


                  显示：  Encoders的静态方法调用
                              java常见的基本数据类型：   Encoders.xxx(类型)
                              scala常见的基本数据类型：  Encoders.scalaxxx

                           自定义的类：
                              样例类：    Encoders.product[T]
                                           ExpressionEncoder[T]()
                              非样例类：  ExpressionEncoder(T)

   */
  @Test
  def testDataSet() : Unit ={

    val list = List(Person("jack", 20), Person("jack", 20), Person("jack", 20), Person("jack", 20))

    // 隐式
    /*import sparkSession.implicits._

    val ds: Dataset[Person] = sparkSession.createDataset(list)

    ds.show()

    // DS是强类型
    val rdd: RDD[Person] = ds.rdd*/

    //val ds: Dataset[Person] = sparkSession.createDataset(list)(Encoders.product[Person])
    val ds: Dataset[Person] = sparkSession.createDataset(list)(ExpressionEncoder[Person]())

    ds.show()



  }

  /*
       def createDataFrame(rowRDD: RDD[Row], schema: StructType): DataFrame

       StructType: 表结构类型！
                      代表表有哪些列，都是什么类型的列
                       def apply(fields: Seq[StructField]): StructType = StructType(fields.toArray)

        StructField： 表结构字段！
                case class StructField(
                  name: String,     //字段名
                  dataType: DataType,  //字段类型
                  nullable: Boolean = true,   //是否允许为Null
                  metadata: Metadata = Metadata.empty   //其他属性)

        DataType：  数据的类型，是抽象的！ 常见的类型的实现，Spark已经提供，例如
                          StringType

   */
  @Test
  def testcreateDataFrame2() : Unit ={

    val list = List(Row("jack", 20), Row("marry", 30))

    val rdd: RDD[Row] = sparkSession.sparkContext.makeRDD(list, 2)

    val structType: StructType = StructType(StructField("name", StringType) :: StructField("age", IntegerType) :: Nil)

    val df: DataFrame = sparkSession.createDataFrame(rdd, structType)

    df.show()

    df.printSchema()


  }



  /*
      Row:  代表关系表中的一行内容！类比为 Array[Any],可以通过索引取出数据！
              弊端： 弱类型，在编译时无法检查从DF中取出的数据类型，在执行时有可能会出错



   */
  @Test
  def testRow() : Unit ={

    //读取json中的数据，封装为DF
    val df: DataFrame = sparkSession.read.json("input/employees.json")

    // 获取DF中的RDD
    val rdd: RDD[Row] = df.rdd

    //val rdd1: RDD[(Any, Any)] = rdd.map(row => (row(0), row(1)))   不符合要求
    val rdd1: RDD[(Int, Double)] = rdd.map(row => {
      val ele1: Int = row.getInt(0)
      val ele2: Double = row.getDouble(1)
      (ele1, ele2)
    })

    rdd1.collect()


  }

  /*
      从集合中创建一个DF：
        def createDataFrame[A <: Product : TypeTag](data: Seq[A]): DataFrame
            A 必须是 Product类型

            所有的样例类都是Product类型！
   */
  @Test
  def testNewDataFrame() : Unit ={

    val list = List(Person("jack", 20), Person("jack", 20), Person("jack", 20), Person("jack", 20))

    val df: DataFrame = sparkSession.createDataFrame(list)

    df.show()

    // not ok
   /* val list = List(1, 2, 3, 4)

    val df: DataFrame = sparkSession.createDataFrame[Int](list)

    df.show()*/


  }

  /*
        SQL :    有指定的执行顺序。
                    熟悉sql的每一个功能，局限性！

        代码：   自上而下，顺序执行
                    灵活！适合人的思维！学习DSL的API
        
        使用代码分析DF中的数据，Sparksql提供的这个功能，也称为 DSL 语法！
   */
  @Test
  def testDSL() : Unit ={

    val df: DataFrame = sparkSession.read.json("input/employees.json")
    // 只查询name列
    df.select("name").show()

    println("------------------")

    // 查询salary > 4000的员工
    df.select("name","salary").filter(" salary > 4000 ").show()

    //求平均工资
    println("------------------")
    df.agg("salary" -> "avg").show()

    // 求所有人的信息，给所有人的工资 +1000
    println("------------------")

    // 少个东西
    import sparkSession.implicits._

    df.select($"name", $"salary" + 1000).show()
    df.select('name, 'salary + 1000).show()


  }

  /*
      创建DataFrame
                     ① 调用SparkSession提供的方法
                              sparkSession.read : DataFrameReader
                                   DataFrameReader： 读取各种各样的数据文件，封装为DF
                     ② 直接 new(不推荐)
                     ③ SparkSession.createDataFrame

          获取DF中的RDD，表结构，查看数据，映射表，执行sql

   */
  @Test
  def testCreateDF() : Unit ={

    //读取json中的数据，封装为DF
    val df: DataFrame = sparkSession.read.json("input/employees.json")

    // 获取DF中的RDD
    val rdd: RDD[Row] = df.rdd

    //获取表结构
    df.printSchema()

    // 映射表
    df.createTempView("emp")

    //执行sql   类似提交Job
    val result: DataFrame = sparkSession.sql("select * from emp")

    //查看结果集
    result.show()


  }


  /*
        创建SparkSession:
                ①如果当前环境已经创建好了，可以通过以下方法直接获取已经存在的SparkSession
                     SparkSession.builder().getOrCreate()

                ②使用builder，直接创建
                       SparkSession.builder
               *     .master("local")
               *     .appName("Word Count")
               *     .config("spark.some.config.option", "some-value")
               *     .getOrCreate()


               ③基于一个已经存在的SparkConf，创建SparkSession
                  SparkSession.builder().config(conf).getOrCreate()

        SparkSession和SparkContext的关系：
            SparkSession包含SparkContext，通过sparkSession.sparkContext获取！
   */
  @Test
  def testCreateSparkSession() : Unit ={

    // 直接构建
    /*val sparkSession: SparkSession = SparkSession.builder
      .master("local[*]")
      .appName("sql")
      .getOrCreate()

    //获取已经存在的SparkSession
    val sparkSession1: SparkSession = SparkSession.builder().getOrCreate()


    println(sparkSession == sparkSession1)*/


   // val sparkContext = new SparkContext(conf)

   // val sparkSession: SparkSession = SparkSession.builder().config(conf).getOrCreate()

    //println(sparkSession.sparkContext)


  }

  val sparkSession: SparkSession = SparkSession.builder
    .master("local[*]")
    .appName("sql")
    .getOrCreate()

 // val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("My app")


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
