package com.atguigu.spark.core.app

import com.atguigu.spark.core.beans.UserVisitAction
import org.apache.spark.rdd.RDD

/**
 * Created by VULCAN on 2020/12/4
 */
object Funciton1Demo2 extends  BaseApp {

  override val outputPath: String = "coreExec/Funciton1Demo2"

  def main(args: Array[String]): Unit = {

    runApp{

      val rdd: RDD[UserVisitAction] = getAllDatas()
      // 分别求每个类型的点击数，下单数，支付数
      // 一次性求出，点击数，下单数，支付数
      val rdd1: RDD[(String, (Int, Int, Int))] = rdd
        .filter(bean => bean.click_category_id != -1 || bean.click_product_id != -1)
        .map(bean => (bean.click_category_id.toString, 1))
        .reduceByKey(_ + _)
        .map {
          case (category, clickCount) => (category, (clickCount, 0, 0))
        }

      // 一次性求出，点击数，下单数，支付数
      val rdd2: RDD[(String, (Int, Int, Int))] = rdd
        .filter(bean => bean.order_category_ids != null)
        .flatMap(bean => bean.order_category_ids.split(","))
        .map(category => (category, 1))
        .reduceByKey(_ + _)
        .map {
          case (category, orderCount) => (category, (0, orderCount, 0))
        }


      // 一次性求出，点击数，下单数，支付数
      val rdd3: RDD[(String, (Int, Int, Int))] = rdd
        .filter(bean => bean.pay_category_ids != null)
        .flatMap(bean => bean.pay_category_ids.split(","))
        .map(category => (category, 1))
        .reduceByKey(_ + _)
        .map {
          case (category, payCount) => (category, (0, 0, payCount))
        }

      //  union all
      val rdd4: RDD[(String, (Int, Int, Int))] = rdd1.union(rdd2).union(rdd3)

      // 再group by ，之后sum
      val rdd5: RDD[(String, (Int, Int, Int))] = rdd4.reduceByKey {
        case ((cc1, oc1, pc1), (cc2, oc2, pc2)) => (cc1 + cc2, oc1 + oc2, pc1 + pc2)
      }

      // 排序
      val result: Array[String] = rdd5.sortBy(_._2, false).keys.take(10)

      //写出
      sparkContext.makeRDD(result,1).saveAsTextFile(outputPath)



    }

  }
}
