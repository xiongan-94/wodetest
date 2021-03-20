package com.atguigu.spark.core.app

import com.atguigu.spark.core.beans.UserVisitAction
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD

/**
 * Created by VULCAN on 2020/12/5
 *
 * Top10热门品类中每个品类的Top10活跃Session点击统计
 *
 *    Top10热门品类中每个品类的  点击量最高的 Top10 Session统计
 *          ①求Top10热门品类
 *          ②将数据过滤，过滤出点击的前十热门品类的数据
 *          ③将数据封装
 *              ( (catogary,session),1)
 *          ④聚合
 *                 ( (catogary,session),N)
 *          ⑤转换
 *                   (catogary,(session,N))
 *           ⑥分组排序
 *                   (catogary,{(session1,N),(session2,N),(session3,N)....})
 *
 *                   (catogary,{(session1,N),(session2,N),(session3,N)....(session10,N)})
 *
 */
object Function2Demo1 extends  BaseApp {
  override val outputPath: String = "coreExec/Function2Demo1"

  def main(args: Array[String]): Unit = {

    runApp{

      //①求Top10热门品类
      val rdd: RDD[String] = sparkContext.textFile("E:\\0821\\200821BigData\\coreExec\\Funciton1Demo1\\part-00000")
      val Top10HotCatogary: Array[String] = rdd.collect()

      val bc: Broadcast[Array[String]] = sparkContext.broadcast(Top10HotCatogary)

      //②将数据过滤，过滤出点击的前十热门品类的数据
      val rdd1: RDD[UserVisitAction] = getAllDatas()

      // 过滤点击的数据
      val rdd2: RDD[UserVisitAction] = rdd1.filter(bean => bean.click_category_id != -1)
      //过滤前十热门品类点击的数据
      val rdd3: RDD[UserVisitAction] = rdd2.filter(bean => bc.value.contains(bean.click_category_id.toString))

      // ③将数据封装
      val rdd4: RDD[(Long, Iterable[(String, Int)])] = rdd3
        .map(bean => ((bean.click_category_id, bean.session_id), 1))
        .reduceByKey(_ + _)
        .map {
          case ((catogary, session), count) => (catogary, (session, count))
        }.groupByKey()

      // 排序
      val result: RDD[(Long, List[(String, Int)])] = rdd4.mapValues(iter => iter.toList.sortBy(-_._2).take(10))

      result.coalesce(1).saveAsTextFile(outputPath)





    }


  }
}
