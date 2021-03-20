package com.atguigu.spark.day03

/**
 *
 *        RDD可以调用的通用的转换算子
 */

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.junit._

import scala.collection.mutable.ListBuffer
import scala.reflect.ClassTag

class CommonRDDOperationTest {

  /*
   def map[U: ClassTag](f: T => U): RDD[U] = withScope {
   // 检查是否存在闭包，如果存在，确保闭包变量可以序列化，发送给Task
    val cleanF = sc.clean(f)
    new MapPartitionsRDD[U, T](this, (_, _, iter) => iter.map(cleanF))

    作用：将RDD中的每一个元素，都调用函数，返回新的类型的元素的RDD
          不会改变元素的分区！不会产生shuffle！
  }

   */
  @Test
  def testMap() : Unit ={

    val list = List(1, 2, 3, 4)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)

    // Driver端的main()的栈空间
    var i = "hello " ;

    //  代码的编写在Driver的main中，算子的执行在executor端
    val rdd1: RDD[String] = rdd.map(ele => i + ele)

    rdd1.saveAsTextFile("output")

  }

  //从服务器日志数据apache.log中获取用户请求URL资源路径
  @Test
  def testMapExec() : Unit ={

    sparkContext.textFile("input/apache.log",1).
      map(line => line.split(" ")(6)).
      saveAsTextFile("output")

  }

  /*
    iter: 一个分区的迭代器
     MapPartitions：  (_, _, iter: Iterator[T]) => cleanedF(iter)
          批处理！ 一个分区处理一次！ 一个分区调用cleanedF()一次！
          输出的结果的个数: 分区个数

     map:              (_, _, iter) => iter.map(cleanF)
          一个一个处理！ 分区中的每个元素都调用一次cleanF()!
            输出的结果的个数: 元素的个数
   */
  //小功能：获取每个数据分区的最大值
  @Test
  def testMapPartitionsExec() : Unit ={

    val list = List(1, 2, 3, 4)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)

   // val rdd2: RDD[Int] = rdd.mapPartitions(iter => List(iter.max).iterator)

    val rdd2: RDD[Int] = rdd.mapPartitions(iter => Iterator(iter.max))

    rdd2.saveAsTextFile("output")

  }


  /*
        和map的区别
            在某些场景下，只能用mapParitions不能用map,例如连接数据库！
   */
  @Test
  def testMapPartitions() : Unit ={

    val list = List(1, 2, 3, 4)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)

    // 没有实现序列化，必须写在算子里面，不能构成闭包
    val connection: Connection = DriverManager.getConnection("jdbc:mysql://hadoop103:3306/gmall", "root", "000000")

    // 创建了四个连接
    /*rdd.map(ele => {

      // ....
      val ps: PreparedStatement = connection.prepareStatement("select * from tbl_user")

    })*/

    // 创建了两个连接
    val rdd2: RDD[String] = rdd.mapPartitions(iter => {

    //  val connection: Connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/0508", "root", "guodai")
      // 建立连接
      //conn.preparement(sql)
      val ps: PreparedStatement = connection.prepareStatement("select * from tbl_user")

      val resultSet: ResultSet = ps.executeQuery()

      val result: ListBuffer[String] = ListBuffer[String]()

      while (resultSet.next()) {
        result.append(resultSet.getString("username"))
      }

      result.iterator
    })

    rdd2.collect()

  }

  /*
      mapPartitions 更灵活，传入一个分区，可以输出任意一个 集合！


   */
  @Test
  def testMapDiffMapPartitions() : Unit ={

    val list = List(1, 2, 3, 4)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)

    val rdd2: RDD[Int] = rdd.map(ele => ele + 1)

    // 返回RDD中元素的数量
    println(rdd2.count())

    val rdd3: RDD[Nothing] = rdd.mapPartitions(iter => Nil.iterator)

    println(rdd3.count())

  }



  //小功能：获取每个数据分区的最大值,输出时同时输出索引号  (分区索引号，最大值)
  @Test
  def testMapPartitionsWithIndex() : Unit ={

    val list = List(1, 2, 3, 4)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)

    // iter : 一个分区
    val rdd2: RDD[(Int, Int)] = rdd.mapPartitionsWithIndex((index, iter) => List((index, iter.max)).iterator)

    rdd2.saveAsTextFile("output")

  }

  //获取第二个数据分区的数据
  @Test
  def testMapPartitionsWithIndexExec() : Unit ={

    val list = List(1, 2, 3, 4)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)

    val rdd1: RDD[Int] = rdd.mapPartitionsWithIndex((index, iter) => {
      //只要分区号为1的分区的数据
      // 保证if和else的返回值类型是一样的
      if (index == 1) {
        // 就是一个Iterator
        iter
      } else {
        //不能返回数据
        Nil.iterator
      }

    })

    rdd1.saveAsTextFile("output")

  }

  /*
小功能：将List(List(1,2),3,List(4,5))进行扁平化操作

FlatMap= 先map , 再flatten
   */
  @Test
  def testFlatMap() : Unit ={

    val rdd: RDD[Any] = sparkContext.makeRDD(List(List(1, 2), 3, List(4, 5)), 2)

    //保证集合中元素的 嵌套层数 是一致的！
    val rdd2: RDD[Int] = rdd.flatMap(ele => {
      ele match {
        case a: List[Int] => a
        case b: Int => List(b)
      }
    })

    rdd2.saveAsTextFile("output")

  }


  /*
        将每个分区都转换为数组，返回新的RDD
   */
  @Test
  def testGlom() : Unit ={

    val list = List(1, 2, 3, 4)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)

    val rdd1: RDD[Array[Int]] = rdd.glom()

    rdd1.collect().foreach(array => println(array.mkString(",")))

  }


  //小功能：计算所有分区最大值求和（分区内取最大值，分区间最大值求和）
  @Test
  def testGlomExec() : Unit ={

    val list = List(1, 2, 3, 4)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)

    val rdd1: RDD[Array[Int]] = rdd.glom()

    val rdd2: RDD[Int] = rdd1.map(array => array.max)

    //行动算子
    println(rdd2.sum())

  }


  /*
      def groupBy[K](f: T => K): RDD[(K, Iterable[T])]
          先调用f，将原始RDD的T元素，转换为K类型，将转换后的结果作为key分组，将key对应的原始RDD的T的集合作为value

          List(1, 2, 3, 4) :  T:Int
            ①先调用f(ele / 2)，将原始RDD的T元素，转换为K类型
                0,1,1,2
            ②将转换后的结果作为key分组
                0， {1}
                1,  {2,3}
                2,  {4}



   */
  @Test
  def testGroupBy() : Unit ={

    val list = List(1, 2, 3, 4)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)

    println(rdd.partitioner)

    // 创建ShuffledRDD，此时RDD中的元素是shuffle得到
    val rdd1: RDD[(Int, Iterable[Int])] = rdd.groupBy(ele => ele / 2)

    println(rdd1.partitioner)

    rdd1.saveAsTextFile("output")

    //Thread.sleep(1000000)

  }

  /*
      探讨默认分区器
   */
  @Test
  def testGroupBy1() : Unit ={

    val list = List(1, 2, 3, 4)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)
    // None
    println(rdd.partitioner)
    val rdd1: RDD[(Int, Int)] = rdd.map(ele => (ele, 1))

    // None
    println(rdd1.partitioner)

    val rdd2: RDD[(Int, Int)] = rdd1.reduceByKey(_ + _)

    //Some(org.apache.spark.HashPartitioner@2)
    println(rdd2.partitioner)

    // 创建ShuffledRDD，此时RDD中的元素是shuffle得到
    val rdd3: RDD[(Int, Iterable[(Int, Int)])] = rdd2.groupBy(ele => ele._1)

    //Some(org.apache.spark.HashPartitioner@2)
    println(rdd3.partitioner)

    rdd3.saveAsTextFile("output")

    //Thread.sleep(1000000)

  }

  // 将List("Hello", "hive", "hbase", "Hadoop")根据单词首写字母进行分组
  @Test
  def testGroupByExec1() : Unit ={

    val rdd: RDD[String] = sparkContext.makeRDD(List("Hello", "hive", "hbase", "Hadoop"), 2)

    rdd.groupBy(word => word(0)).saveAsTextFile("output")


  }

  //小功能：从服务器日志数据apache.log中获取每个时间(小时)段(不考虑日期)访问量。
  @Test
  def testGroupByExec2() : Unit ={

    val rdd: RDD[String] = sparkContext.textFile("input/apache.log")

    //按照小时分
    val rdd1: RDD[(String, Iterable[String])] = rdd.groupBy(line => line.split(":")(1))

    val rdd2: RDD[(String, Int)] = rdd1.map {
      case (hour, datas) => (hour, datas.size)
    }

    rdd2.saveAsTextFile("output")

  }

  @Test
  def testHash() : Unit ={

    println("19".hashCode % 2)
  }

  /*
小功能：从服务器日志数据apache.log中获取2015年5月17日的请求路径
   */
  @Test
  def testFilter() : Unit ={

    val rdd: RDD[String] = sparkContext.textFile("input/apache.log")

    rdd.filter(line => line.contains("17/05/2015"))
     // .map(line => line.split(" ")(6))
      .saveAsTextFile("output")

  }

  /*
      用来从一个大的数据集中，抽取样本，观察样本特征！

     def sample(
      withReplacement: Boolean,
            true: 允许重复抽取同一个元素！
            false : 不允许重复抽取同一个元素！
      fraction: Double,  样本 和 数据集的比例 ，必须  >= 0
      seed: Long = Utils.random.nextLong
            seed一样，多次抽取的结果就一样！
      ): RDD[T]
   */
  @Test
  def testSample() : Unit ={

    val list = List(1, 2, 3, 4,5,6,7,8,9)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)

    println(rdd.sample(true, 1,3).collect().mkString(","))
    println(rdd.sample(true, 1,5).collect().mkString(","))
    println(rdd.sample(false, 1).collect().mkString(","))

  }

  /*
       如果不用该算子，你有什么办法实现数据去重？  groupBy
            select
              distinct(id)
              from xx

              select
                id
              from xxx
              group by id

        产生shuffle
   */
  @Test
  def testDistinct1() : Unit ={

    val list = List(1, 2, 3, 4,4,4,4)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)

    // 等价于 distinct(2)
    rdd.distinct.saveAsTextFile("output")

    Thread.sleep(10000000)

  }

  /*
        去重不产生shuffle
   */
  @Test
  def testDistinct2() : Unit ={

    val list = List(1, 2, 3, 4,4,4,4)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)

    val rdd1: RDD[(Int, Int)] = rdd.map(ele => (ele, 1))


    val rdd2: RDD[(Int, Int)] = rdd1.reduceByKey(_ + _, 4)

    //Some(org.apache.spark.HashPartitioner@4)
    println(rdd2.partitioner)

    //
    rdd2.distinct(3).saveAsTextFile("output")

    Thread.sleep(10000000)

  }

  /*

      repartition=coalesce(numPartitions, shuffle = true)

      建议：  分区由多 合并 到少，使用coalesce
                由少到多，使用repartition
   */
  @Test
  def testCoalesce() : Unit ={

    val list = List(1, 2, 3, 4,4,4,4,6)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 4)

   // rdd.coalesce(2).saveAsTextFile("output")

    //请求大数量的分区，保持当前分区
    //rdd.coalesce(10).saveAsTextFile("output")

    // 如果需要由 少的分区合并到多的分区，需要传入shuffle=true
    rdd.coalesce(10,true).saveAsTextFile("output")

    Thread.sleep(1000000)

  }

  /*
          先调用函数将 T => K，再对K类型进行排序！


          结果是Range分区

          有shuffle！
   */
  @Test
  def testSortBy() : Unit ={

    val list = List(11, 22, 3, 4,4,4,4,6)

    val rdd: RDD[Int] = sparkContext.makeRDD(list, 2)

   // val rdd1: RDD[Int] = rdd.sortBy(ele => ele + "")

    //降序排
    val rdd1: RDD[Int] = rdd.sortBy(ele => ele + "",false,1)

    rdd1.saveAsTextFile("output")

  }


  /*
        无比较，不排序！ 排序都需要要求排序的对象，可以进行比较！
            java中，提供Comparable<T>  可比较的，如果A类是可比较的，A类实现Comparable中的compare方法

                        Comparator<T>  比较器。 如果希望比较A类，可以使用非侵入式(不改变A的代码)！
                                        只需要提供   Comparator<A>


          scala：
        Ordering:  比较器！排序器！
        Ordered: 可排序的！可比较的！


        ClassTag:  类的标记
            泛型：   约束！ 约束当前类的某些参数的类型！ 主要在编译时使用！

            在编译完成后，运行时，泛型被擦除！

            场景： 需要使用反射工具，通过反射创建 对象！
                      核心： 拿到创建的对象的类型！
                      特殊：  Array[T]
   */
  @Test
  def testSortByEmp() : Unit ={

    val list = List(Emp("jack1", 21), Emp("marry", 30), Emp("tom", 20), Emp("jack1", 20))

    val rdd: RDD[Emp] = sparkContext.makeRDD(list, 2)
    // sortBy 只排 K  类型，需要将T类型中需要排序的部分，转换为K类型



    //  按照名称升序排，名称相同的，按照年龄升序排
   // rdd.sortBy(emp => (emp.name, emp.age),true,1).saveAsTextFile("output")


    //  按照名称降序排，名称相同的，按照年龄降序排
    //rdd.sortBy(emp => (emp.name, emp.age),false,1).saveAsTextFile("output")

    /*
        按照名称升序排，名称相同的，按照年龄降序排
            要比较的类型是：  (emp.name, emp.age) Tuple2 : K
                    Ordering[K] :  Ordering[Tuple2]

     */
    /*rdd.sortBy(emp => (emp.name, emp.age),numPartitions=1)(
      Ordering.Tuple2[String,Int](Ordering.String,Ordering.Int.reverse),
      ClassTag(classOf[Tuple2[String,Int]])
    ).saveAsTextFile("output")

     */

    //按照名称降序排，名称相同的，按照年龄升序排
    rdd.sortBy(emp => (emp.name, emp.age),false,numPartitions=1)(
      Ordering.Tuple2[String,Int](Ordering.String,Ordering.Int.reverse),
      ClassTag(classOf[Tuple2[String,Int]])
    ).saveAsTextFile("output")


  }

  @Test
  def testSortByOrdering() : Unit ={

    val list = List(Emp("jack1", 21), Emp("marry", 30), Emp("tom", 20), Emp("jack1", 20))

    val rdd: RDD[Emp] = sparkContext.makeRDD(list, 2)

    // 将Emp整体作为要排序的类型，提供Emp类型的Ordering对象
    val orderingEmp: Ordering[Emp] = new Ordering[Emp]() {

      //按照名称降序排，名称相同的，按照年龄升序排
      override def compare(x: Emp, y: Emp): Int = {

        var result: Int = -x.name.compareTo(y.name)

        if (result == 0){
          //按照年龄升序排
          result= x.age.compareTo(y.age)
        }
        result
      }
    }

    //按照名称降序排，名称相同的，按照年龄升序排
    rdd.sortBy(e => e,numPartitions = 1)(
      orderingEmp,ClassTag(classOf[Emp])
    ).saveAsTextFile("output")


  }


  @Test
  def testSortByOrdered() : Unit ={

    val list = List(Emp1("jack1", 21), Emp1("marry", 30), Emp1("tom", 20), Emp1("jack1", 20))

    val rdd: RDD[Emp1] = sparkContext.makeRDD(list, 2)
    //按照名称升序排，名称相同的，按照年龄降序排
    rdd.sortBy(e => e,numPartitions = 1).saveAsTextFile("output")


  }

  /*
      允许使用外部命令(shell,python)处理RDD中的数据！
        输入RDD：
        处理逻辑： 命令(shell,python)
                      ①将RDD中的元素读入到命令中
                            read: 从标准输入中读取输出

                      ②输出
                          echo :  将输出输出到标准输出
        输出RDD：

   */
  @Test
  def testPipe() : Unit ={



   //sc.makeRDD(List(11, 22, 3, 4), 2).pipe("/home/atguigu/processdata").collect.mkString(",")

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

case class Emp(name:String,age:Int)

case class Emp1(name:String,age:Int) extends  Ordered[Emp1] {
  ////按照名称升序排，名称相同的，按照年龄降序排
  override def compare(that: Emp1): Int = {

    var result: Int = this.name.compareTo(that.name)

    if (result == 0){
      result = -this.age.compareTo(that.age)
    }
    result

  }
}