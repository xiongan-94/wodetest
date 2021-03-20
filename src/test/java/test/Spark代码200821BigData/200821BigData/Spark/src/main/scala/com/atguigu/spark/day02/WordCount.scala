package com.atguigu.spark.day02

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}


/**
 * Created by VULCAN on 2020/11/27
 *
 *    HadoopMR :
 *        Mapper:  将数据进行map操作   1对1
 *                  输入 ： 猴子----->Mapper.map()-----> 人
 *                  一行内容 ----->Mapper.map() -------> {(单词,1),(单词,1)}
 *
 *                  将输入的数据转换为想要处理的数据！
 *        Reducer: 将Mapper输出的结果，进行聚合！得到最终的结果！
 *
 *        Mapper，Reducer，Combiner,Paritioner 称为 Hadoop中MR的编程模型！
 *
 *
 *        步骤： ①将编程思路，按照Hadoop提供的编程模型实现
 *               ②编写Driver
 *                     Driver中完成Job的执行计划
 *                        a) : 设置有哪些组件
 *                              Job.setMapper(xxx)
 *                              Job.setReducer(xx)
 *                        b) : 设置组件之间的衔接关系
 *                              Job.setMapOutPutKeyClass()
 *
 *                              FileInputFormat.setInputPath(xxx)
 *
 *                     Job.waitForCompletion()  ------> 提交Job
 *                        提交Job之前，需要生成Job的执行计划:
 *                            a) JobContextImpl :  应用上下文
 *                                job.xml:  封装了所有的配置参数
 *                                split.info : 切片信息
 *
 *                            b) 执行
 *
 *
 *         编写一个Job的核心，就是编写Job的执行计划(Jobcontext)
 *              Job  ----------->  JobContextImpl
 *
 *
 *      Spark:    SparkContext(Spark应用上下文)
 *                编程模型：
 *                    core :   RDD(最多) , 累加器 , 广播变量
 *
 *
 *
 *
 *
 *
 */
object WordCount {

  def main(args: Array[String]): Unit = {

    /*
          setMaster: 设置应用运行的集群
                master url:
                    local: 本地单线程(单核)
                    local[4]： 本地4核
                    spark://master:7077： 运行在standalone集群
                    yarn: 运行在yarn上
                    mesos:  运行在mesos


           setAppName: 为app设置name，name会在web UI显示

           SparkConf: 代表整个app的配置对象，会自动加载以spark开头的参数和 系统参数，可以通过set()修改或设置默认参数！

     */
    val conf: SparkConf = new SparkConf().setMaster("local").setAppName("My app")


    /*
          sparkContext: 是一个spark应用的核心！
                          作用：①代表和集群的 一个连接，负责将Job提交到集群
                                ②用来创建 编程模型： RDD(最多) , 累加器 , 广播变量


          一个JVM只允许运行一个sparkContext！
                                RDD理解为一个List集合
     */
   // val sparkContext1 = new SparkContext(conf)
    val sparkContext = new SparkContext(conf)

    // 创建一个RDD  main中的相对路径相对于 project    文本中的一行内容 hi hi hi hi
    val rdd: RDD[String] = sparkContext.textFile("input")


    //  hi
    val rdd1: RDD[String] = rdd.flatMap(line => line.split(" "))

    //  （hi，1）
    val rdd2: RDD[(String, Int)] = rdd1.map(word => (word, 1))

    println(rdd2.partitioner)

    // 聚合    reduceByKey :  将相同key的value，按照指定的聚合函数进行聚合
    // （hi，N）
    val rdd3: RDD[(String, Int)] = rdd2.reduceByKey((v1: Int, v2: Int) => v1 + v2,3)

    println(rdd3.getNumPartitions)

    println(rdd3.partitioner)

    // 将结果打印 或 将结果写出到文件
    // collect: 将分布式计算的结果收集到当前线程
    println(rdd3.collect().mkString(","))
    println(rdd3.collect().mkString(","))


    Thread.sleep(100000000)


    //app 运行完毕，可以调用stop终止
    sparkContext.stop()


  }

}
