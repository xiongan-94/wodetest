package com.atguigu.spark.core.app

import com.atguigu.spark.core.beans.UserVisitAction
import org.apache.spark.rdd.RDD

/**
 * Created by VULCAN on 2020/12/4
 */
object Funciton1Demo1 extends  BaseApp {

  override val outputPath: String = "coreExec/Funciton1Demo1"

  def main(args: Array[String]): Unit = {

    runApp{

      val rdd: RDD[UserVisitAction] = getAllDatas()
      // 分别求每个类型的点击数，下单数，支付数
      // 点击数
      val rdd1: RDD[(String, Int)] = rdd
        .filter(bean => bean.click_category_id != -1 || bean.click_product_id != -1)
        .map(bean => (bean.click_category_id.toString, 1))
        .reduceByKey(_ + _)

      // 下单数
      val rdd2: RDD[(String, Int)] = rdd
        .filter(bean => bean.order_category_ids != null)
        .flatMap(bean => bean.order_category_ids.split(","))
        .map(category => (category, 1))
        .reduceByKey(_ + _)

      // 支付数
      val rdd3: RDD[(String, Int)] = rdd
        .filter(bean => bean.pay_category_ids != null)
        .flatMap(bean => bean.pay_category_ids.split(","))
        .map(category => (category, 1))
        .reduceByKey(_ + _)


      // leftJoin
      val rdd4: RDD[(String, ((Int, Option[Int]), Option[Int]))] = rdd1.leftOuterJoin(rdd2).leftOuterJoin(rdd3)

      val rdd5: RDD[(String, (Int, Int, Int))] = rdd4.map {
        case (category, ((clickCount, orderCount), payCount)) => (category, (clickCount, orderCount.getOrElse(0), payCount.getOrElse(0)))
      }

      // 排序
      val result: Array[String] = rdd5.sortBy(_._2, false).keys.take(10)

      //写出
      sparkContext.makeRDD(result,1).saveAsTextFile(outputPath)



    }

  }
}
