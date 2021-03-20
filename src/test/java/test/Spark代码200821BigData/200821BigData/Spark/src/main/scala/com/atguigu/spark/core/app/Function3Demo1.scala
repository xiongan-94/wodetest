package com.atguigu.spark.core.app

import java.text.DecimalFormat

import com.atguigu.spark.core.beans.UserVisitAction
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD

/**
 * Created by VULCAN on 2020/12/5
 */
object Function3Demo1 extends BaseApp {
  override val outputPath: String = "coreExec/Function3Demo1"

  def main(args: Array[String]): Unit = {

    //求当前数据中所有的页面被访问的次数  Map[String,Int]
    val rdd1: RDD[UserVisitAction] = getAllDatas()

    val pagesMap: Map[Long, Int] = rdd1
      .map(bean => (bean.page_id, 1))
      .reduceByKey(_ + _)
      .collect().toMap

    val bc: Broadcast[Map[Long, Int]] = sparkContext.broadcast(pagesMap)

    //求每个页面单跳的次数
    //求出每个用户，每个SESSION访问的记录，之后按照访问时间升序排序
    val rdd2: RDD[Iterable[(String, Long)]] = rdd1
      .map(bean => ((bean.user_id, bean.session_id), (bean.action_time, bean.page_id)))
      .groupByKey()
      .values
      .map(iter => iter.toList.sortBy(_._1))

    //按照排序的结果求 页面的访问顺序,拆分页面单跳
    //  Iterable[Long] 都是一个访问顺序的页面ID集合
    val rdd3: RDD[Iterable[Long]] = rdd2.map(iter => iter.map(_._2))
    // 拆分单跳
    val rdd4: RDD[(Long, Long)] = rdd3.flatMap(iter => iter.zip(iter.tail))
    // 聚合所有的单跳记录，求出每个单跳的次数
    val rdd5: RDD[((Long, Long), Int)] = rdd4.map(x => (x, 1)).reduceByKey(_ + _)

    val format = new DecimalFormat("0.00%")

    //求转换率
    rdd5.map{
      case ((fromPage, toPage), count) =>   fromPage+"-"+toPage+": "+format.format(count.toDouble / bc.value.getOrElse(fromPage,0))
    }.coalesce(1).saveAsTextFile(outputPath)

  }
}
