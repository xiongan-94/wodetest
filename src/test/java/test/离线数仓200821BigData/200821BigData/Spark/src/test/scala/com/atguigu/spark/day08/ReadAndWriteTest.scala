package com.atguigu.spark.day08

import java.util.Properties

import com.atguigu.spark.day07.Person
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.junit._

/**
 * Created by VULCAN on 2020/12/7
 */
class ReadAndWriteTest {

  @Test
  def testMysql() : Unit ={

    val properties = new Properties()

    properties.put("user","root")
    properties.put("password","000000")

    val df: DataFrame = sparkSession.read.jdbc("jdbc:mysql://hadoop103:3306/gmall", "base_region", properties)

    //全表查询   show()只查询前20行
    df.show()

    // mysql中的表，需要创建一个临时表名映射
    df.createTempView("base_region")

    // 查询指定的数据集合
    val df1: DataFrame = sparkSession.sql("select * from base_region where id > 5")

    //写
    df1.write.jdbc("jdbc:mysql://hadoop103:3306/gmall?useUnicode=true&characterEncoding=UTF-8","base_region2",properties)



  }

  /*
        CSV:  逗号分割的数据集！
              xxx符号分割的数据集

        TSV: tab分割的数据集！

        #SV :  #分割的数据集！

         DataFrameReader.scala  705行  规定了读取Json文件时，可以传入的参数！


   */
  @Test
  def testCSV() : Unit ={

    val df: DataFrame = sparkSession.read
      // 是否将首行作为列名
      .option("header","true").option("sep","#").
      csv("input/person.csv")

    df.show()


    df.write.mode("append").option("sep",":").csv("output1")


  }

  /*
        专用的：  方法只能写某种格式
        通用的：  方法通过改变参数，可以写多种格式！

        默认一个JSON对象必须保存为一行！

        DataFrameReader.scala  458行  规定了读取Json文件时，可以传入的参数！



   */
  @Test
  def testJson() : Unit ={

    // 通用的读
    sparkSession.read.format("json").load("input/employees.json")

    val df: DataFrame = sparkSession.read.
      option("multiLine","false").
      option("lineSep","#").
      json("input/employees.json")

    df.show()

  /*  val list = List(Person("jack", 20), Person("jack", 20), Person("jack", 20), Person("jack", 20))

    val df1: DataFrame = sparkSession.createDataFrame(list)

    df1.printSchema()

    //专用的
    df1.write.json("output")

    //通用的写
    df1.write.format("parquet").save("output")*/



  }

  /*
        写parquet: df.write.save("output")
          在写出时不指定格式，默认就散parquet!

        读parquet: sparkSession.read.load("路径")
   */
  @Test
  def testParquet() : Unit ={


   val list = List(Person("jack", 20), Person("jack", 20), Person("jack", 20), Person("jack", 20))

    val df: DataFrame = sparkSession.createDataFrame(list)

    df.show()

    df.write.save("output")

   // val df: DataFrame = sparkSession.read.load("input/employees.json")


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
