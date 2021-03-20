package com.atguigu.spark.day06

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.hbase.{Cell, CellUtil, HBaseConfiguration}
import org.apache.hadoop.hbase.client.{Put, Result}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.{TableInputFormat, TableOutputFormat}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.mapreduce.Job
import org.apache.spark.rdd.{JdbcRDD, NewHadoopRDD, RDD}
import org.apache.spark.{SparkConf, SparkContext}
import org.junit._

/**
 * Created by VULCAN on 2020/9/7
 *      本章节介绍使用Spark读写数据
 */
class ReadAndWriteTest {

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
          读写文本数据：
                读：SparkContext.textFile()
                写： RDD.saveAsTextFile()
   */

  /*
         读写对象文件： 将RDD中的数据以对象的形式序列化保存到文件中！
                  写：   RDD.saveAsObjectFile()
                  读：   sparkContext.objectFile

   */
  @Test
  def test() : Unit ={

    val list = List(1, 2, 3, 4)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)

    //写
    rdd.saveAsObjectFile("obj")

    //读
    val rdd1: RDD[Int] = sparkContext.objectFile[Int]("obj")

    println(rdd1.collect().mkString(","))



  }

  /*
         读写SF文件：   SequenceFile是Hadoop生态中一种保存数据的格式！
                          保存的数据必须是K-V对。
                  写：   RDD.saveAsSequenceFile()
                  读：   sparkContext.sequenceFile

   */
  @Test
  def testSF() : Unit ={

    val list = List(1, 2, 3, 4)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)

    val rdd1: RDD[(String, Int)] = rdd.map(x => ("a", x))

    //写
    rdd1.saveAsSequenceFile("sf")

    //读
    val rdd2: RDD[(String, Int)] = sparkContext.sequenceFile[String, Int]("sf")

    println(rdd2.collect().mkString(","))



  }

  /*
        读JDBC
                  以Mysql为例

  class JdbcRDD[T: ClassTag](
    sc: SparkContext,
    getConnection: () => Connection,    // 返回Connection的空参函数
    sql: String,   //执行的查询的sql   sql必须包含两个占位符，用来填充边界
    lowerBound: Long,     下界，填充第一个占位符
    upperBound: Long,     上界， 填充第二个占位符
    numPartitions: Int,   分N个区
    mapRow: (ResultSet) => T = JdbcRDD.resultSetToObjectArray _) ：  代表一个函数，函数将resultSet中的一行记录封装为一个Bean，不要调用next,系统会自动调！

  extends RDD[T](sc, Nil) with Logging


   */
  @Test
  def testJDBCRead() : Unit ={

    val rdd = new JdbcRDD[Region](
      sparkContext,
      () => DriverManager.getConnection("jdbc:mysql://hadoop103:3306/gmall?useUnicode=true&characterEncoding=UTF-8", "root", "000000"),
      "select * from base_region where id >= ?  and  id <=?",
      2,
      6,
      3,
      (rs: ResultSet) => {
        //一行数据的封装逻辑
        Region(rs.getString("id"), rs.getString("region_name"))
      }

    )

    rdd.saveAsTextFile("output")

  }

  /*
       写JDBC
                 以Mysql为例


  */
  @Test
  def testJDBCWrite() : Unit ={

    val list = List(Region("8", "华南北"), Region("9", "华北南"))

    val rdd: RDD[Region] = sparkContext.makeRDD(list, 2)

    //iter代表一个分区
    rdd.foreachPartition(iter => {
      //创建连接
      val connection: Connection = DriverManager.getConnection("jdbc:mysql://hadoop103:3306/gmall?useUnicode=true&characterEncoding=UTF-8", "root", "000000")

      //准备sql
      val sql ="insert into  base_region values(?,?)"

      //预编译
      val ps: PreparedStatement = connection.prepareStatement(sql)

      //遍历每个元素，填充占位符，写出
      iter.foreach(region =>{
        ps.setString(1,region.id)
        ps.setString(2,region.regionName)

        //执行写入
        ps.executeUpdate()
      })

      //关闭资源
      ps.close()
      connection.close()
    })

  }

  /*
        HBase:
                读或写，都需要有Connection(和HBase集群的连接)。

                Table:  代表一张表
                  插入或更新：  Table.put(Put put)

                  读取：  get / scan
                              get： 读取单行
                              scan : 读取多行

                              每行的数据都会封装到一个Result
                              一个Result代表一行，一行由多个Cell（单元格）

                工具类：  Bytes: 将常见的基本数据类型和Byte[]做转换
                                .toBytes() : 将xxx转换为Byte[]
                                .toxxx   ： 将Bytep[]转换为xxx类型


                          CellUtil（可选）: 从Cell  中取出指定的元素！
                                              取rowKey: CellUtil.cloneRowKey
                                                          等价于 Cell.getRowkey()



        读HBase： NewHadoopRDD

 class NewHadoopRDD[K, V](
    sc : SparkContext,
    inputFormatClass: Class[_ <: InputFormat[K, V]],   //读取数据的输入格式
    keyClass: Class[K],     // 输入格式中RecordReader的key的类型
    valueClass: Class[V],    // 输入格式中RecordReader的value的类型
    @transient private val _conf: Configuration)   //读取HBase的配置
  extends RDD[(K, V)](sc, Nil) with Logging

  核心：哪个输入格式可以读取HBase中的数据？
            TableInputFormat可以读取hbase的数据！

            new RecordReader<ImmutableBytesWritable, Result>()
                ImmutableBytesWritable: Rowkey
                Result: 一行的内容

   */
  @Test
  def testHBaseRDD() : Unit ={

    //在Configuration中告诉Hbase的集群地址，在这个Job中，使用哪个输入格式，输入格式读取哪个表
    val conf: Configuration = HBaseConfiguration.create()

    conf.set(TableInputFormat.INPUT_TABLE,"t1")

    //设置读取的范围  TableInputFormat

    val rdd = new NewHadoopRDD[ImmutableBytesWritable, Result](
      sparkContext,
      classOf[TableInputFormat],
      classOf[ImmutableBytesWritable],
      classOf[Result],
      conf
    )


    rdd.foreach{
      case (rowKey,result) => {

        //  获取一行的所有的单元格
        val cells: Array[Cell] = result.rawCells()

        for (cell <- cells) {
          println("rowkey:" + Bytes.toString(CellUtil.cloneRow(cell)) +
            "  列名:" + Bytes.toString(CellUtil.cloneFamily(cell)) + ":" +
            Bytes.toString(CellUtil.cloneQualifier(cell)) + "   值:" +
            Bytes.toString(CellUtil.cloneValue(cell)))
        }

      }
    }


  }

  /*
        写hbase ：saveAsNewAPIHadoopDataset(conf: Configuration)

            注意： 只有key-value才能调用

            ①将RDD转换为K-V类型的RDD
                  RDD[Key,Put  ]
                        Key: 自定义，一般取rowKey , 封装 ImmutableBytesWritable
            ②将写出的所有的配置，都放入到Configuration
                  输出格式：  TableOutputFormat<Key,Mutation>
                                      Mutation:所有写操作的父类，例如Put
                  哪个表：
                  输出的Key，value的类型:

   */
  @Test
  def testHBaseWrite() : Unit ={

    val list = List(("r3", "cf1", "name", "marry"), ("r3", "cf1", "age", "30"), ("r4", "cf1", "name", "sarry"), ("r4", "cf1", "age", "30"))

    val rdd: RDD[(String, String, String, String)] = sparkContext.makeRDD(list, 2)

    val conf: Configuration = HBaseConfiguration.create()

    //设置向哪个表写
    conf.set(TableOutputFormat.OUTPUT_TABLE,"t1")

    val job: Job = Job.getInstance(conf)

    //通过Job设置，设置完，再将设置的参数配置取出来
    job.setOutputFormatClass(classOf[TableOutputFormat[ImmutableBytesWritable]])
    job.setOutputKeyClass(classOf[ImmutableBytesWritable])
    job.setOutputValueClass(classOf[Put])


    //将RDD转换为K-V类型的RDD
    val rdd1: RDD[(ImmutableBytesWritable, Put)] = rdd.map {
      case (rowkey, cf, cq, value) => {
        val key = new ImmutableBytesWritable(Bytes.toBytes(rowkey))
        val put = new Put(Bytes.toBytes(rowkey))

        put.addColumn(Bytes.toBytes(cf), Bytes.toBytes(cq), Bytes.toBytes(value))
        (key, put)
      }
    }

    rdd1.saveAsNewAPIHadoopDataset(job.getConfiguration)

  }

}

//case class  User(id:Int,username:String,password:String)
case  class  Region(id:String,regionName: String)