package com.atguigu.spark.day08

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.junit._

/**
 * Created by VULCAN on 2020/12/7
 *
 *      Hive:   将HQL语句 转成 MR 运行！
 *
 *            作用：  对存储在HDFS的结构化数据进行数据分析！
 *
 *            表：
 *
 *                    内部表(管理表)： 删除管理表时，不仅会删除数据还会删除元数据！
 *                    外部表：  删除表时，只删除元数据！
 *
 *                    分区表： 表的内部，根据分区字段，继续将表划分为若干个目录！每个目录都是一个分区，在查询时使用分区字段过滤！
 *                    全量表： 不是分区表
 *                    分桶表：  将一张表使用分桶(将数据根据某一列，分类到文件中)操作
 *
 *                    本地表(native table)：   默认大部分使用的都是本地表(数据在HDFS存储)！
 *                    非本地表(non-native table)：  数据在非HDFS上存储。例如数据在kafka存储，在es存储，在mongdb存储等！
 *                                                  数据在hbase中存储
 *
 *            怎么用：  ①安装
 *                            HDFS，YARN
 *                      ②启动HDFS，启动YARN
 *                      ③启动hive-cli
 *                          hive默认支持两种访问方式：
 *                              hive-cli:   hive  等价于   hive --service cli
 *                              jdbc:   beeline 或其他支持JDBC的客户端 DBeaver ， DataGrip
 *                                        必须开启一个服务hiveserver2
 *
 *                                        开启：  hiveserver2  等价于   hive --service hiveserver2
 *
 *
 *            元数据：  描述数据的数据！
 *                          表，库，函数都是元数据！
 *                          默认在derby中存储，derby不支持多用户多线程访问！将元数据存储在mysql!
 *
 *            数据：  在HDFS上存储！
 *
 *            如何获取元数据：
 *                      读取mysql中的元数据！
 *                      ①驱动
 *                            将Mysql的驱动放入 HIVE_HOME/lib
 *
 *                      ②有查询程序
 *                      ③查询Mysql的四个参数： url,驱动类型,账户名，密码
 *
 *             Hive:
 *                服务端：
 *                      hive-cli:   需要查询元数据
 *                               hive --service cli
 *                      hiveserver2:   需要查询元数据
 *                               hive --service hiveserver2
 *                      hivemetastore:  为metastore客户端提供元数据的查询服务！ 需要查询元数据
 *                              hive --service metastore
 *                              和第三方计算框架配合，帮助第三方计算框架查询hive中的元数据！
 *                              使用这个服务，客户端需要配置  hive.metastore.uris(客户端)
 *
 *
 *
 *                      hive在运行时，自动读取  conf/hive-site.xml文件，读取查询Mysql的四个参数，
 *                      加载 lib下的所有的jar包，有驱动和查询的程序，就可以读取元数据！
 *
 *
 *               hive.metastore.uris的含义指，通过metastore服务查询元数据，只需要指定hive的metastore服务的URL即可！
 *
 *
 *
 *
 *        总结：
 *              1. hive-cli :  要到hive安装的机器使用！
 *                      客户端 +  服务端 ，读取 conf/hive-site.xml文件，配置 4个连接数据库的参数！
 *
 *              2. beeline / 工具  向 hiveserver2发送 SQL
 *                       beeline / 工具：客户端 ，连接上 hiveserver2
 *
 *                       hiveserver2： 服务端。 读取 conf/hive-site.xml文件  conf/hiveserver2-site.xml 配置 4个连接数据库的参数！
 *
 *
 *              3. thirft 客户端 向 hivemetastore服务发送请求
 *                        thirft 客户端：  客户端 ， 连接上  hivemetastore服务即可。 配置 hive.metastore.uris
 *                         hivemetastore： 服务端。 读取 conf/hive-site.xml文件，配置 4个连接数据库的参数！
 *                              和第三方计算框架配合使用！
 *
 *
 *
 *       Spark on Hive :  使用Spark作为计算引擎，编写的是Spark SQL支持的代码，分析Hive管理起来的数据！
 *                HDFS数据  --------->  Hive建好了表，映射到数据 ----------> Spark程序读入 -----------> Spark代码分析
 *
 *
 *                 Hive建好了表，映射到数据：  在hive-cli中完成。
 *                                              在Spark中完成！
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
class HiveTest {

  /*
      写Hive
   */
  @Test
  def testHiveWrite() : Unit ={

    val df: DataFrame = sparkSession.read.json("input/employees.json")


    //df.write.saveAsTable("db3.emp")

    //追加写
    //df.write.mode("append").saveAsTable("db3.emp")

    // 覆盖写
    df.write.mode("overwrite").saveAsTable("db3.emp")

  }

  /*
      读Hive
   */
  @Test
  def testHive() : Unit ={

    //sparkSession.sql("show tables").show()

    /*sparkSession.sql("create database db3")

    sparkSession.sql("show databases").show()*/

    sparkSession.sql("use gmall")

    sparkSession.sql("insert into table ads_back_count values('a','b',400)")

    sparkSession.sql("SELECT * from ads_back_count").show()


  }

  val sparkSession: SparkSession = SparkSession.builder
    .master("local[*]")
    .appName("sql")
    //启动访问hive的支持
    .enableHiveSupport()
    .config("spark.sql.warehouse.dir","hdfs://hadoop102:9820/user/hive/warehouse")
    .getOrCreate()



  // Test之后调用的方法
  @After
  def stop()={

    sparkSession.close()

  }

}
