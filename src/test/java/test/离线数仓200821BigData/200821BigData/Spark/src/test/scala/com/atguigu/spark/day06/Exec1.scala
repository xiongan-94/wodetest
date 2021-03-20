package com.atguigu.spark.day06

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by VULCAN on 2020/12/4
 */
object Exec1 {

  def main(args: Array[String]): Unit = {

    /*
      agent.log：时间戳，省份，城市，用户，广告，中间字段使用空格分隔
            示例： 1516609143867 6 7 64 16

      统计出每一个省份 广告被点击数量Top3  广告
            N 省份

            结果 <= 3 * N

      熟悉数据：  粒度(一行数据代表什么意思)
            当前数据的一行代表一个省份某用户对某广告的一次点击行为！

      思路：
              map :    1516609143867 6 7 64 16  =>  (（省份, 广告 ), 1）
              reduce:        =>  (（省份, 广告 ), N）
               map:           =>    (省份,( 广告 , N））
               reduce:           =>    { (省份1,  { (广告1,N) , (广告2,N)....   }) ,
                                  (省份2,  { (广告1,N) , (广告2,N)....   })
                                  }

               map          =>  { (省份1,  { (广告1,N) , (广告2,N) , (广告3,N)  }) ,
                                  (省份2,  { (广告1,N) , (广告2,N) ,(广告3,N)   })
                                  }

   */

    val sparkContext = new SparkContext(new SparkConf().setMaster("local[*]").setAppName("My app"))

    val rdd: RDD[String] = sparkContext.textFile("input/agent.log")

    //  (省份, List[(广告, 点击量)])
    val rdd2: RDD[(String, Iterable[(String, Int)])] = rdd
      .map(line => {
      val words: Array[String] = line.split(" ")
      ((words(1), words(4)), 1)
    })
      .reduceByKey(_ + _)
      .map {
        case ((province, aids), clickCount) => (province, (aids, clickCount))
      }
      .groupByKey()

    // 按照广告点击量排序后取前3
    val result: RDD[(String, List[(String, Int)])] = rdd2.mapValues(iter => iter.toList.sortBy(-_._2).take(3))

    result.coalesce(1).saveAsTextFile("output")

    sparkContext.stop()



  }

}
