package com.atguigu.spark.core.app

import com.atguigu.spark.core.accs.CategoryAcc
import com.atguigu.spark.core.beans.{AccBean, UserVisitAction}
import org.apache.spark.rdd.RDD

import scala.collection.mutable

/**
 * Created by VULCAN on 2020/12/4
 */
object Funciton1Demo3 extends  BaseApp {

  override val outputPath: String = "coreExec/Funciton1Demo3"

  def main(args: Array[String]): Unit = {

    runApp{

      val rdd: RDD[UserVisitAction] = getAllDatas()
      // 分别求每个类型的点击数，下单数，支付数

      //创建和注册累加器
      val acc = new CategoryAcc

      sparkContext.register(acc,"myAcc")

      rdd.foreach(bean => {

        //判断是否是点击的数据
        if(bean.click_category_id != -1 || bean.click_product_id != -1){
          acc.add(bean.click_category_id.toString,"cc")
          //判断是否是下单的数据
        }else if(bean.order_category_ids != null){
          val categorys: Array[String] = bean.order_category_ids.split(",")
          categorys.foreach(category => acc.add(category,"oc"))
          //判断是否是支付的数据
        }else if(bean.pay_category_ids != null){
          val categorys: Array[String] = bean.pay_category_ids.split(",")
          categorys.foreach(category => acc.add(category,"pc"))
        }

      })

      //获取累加器的结果
      val resultMap: mutable.Map[String, AccBean] = acc.value

      val result: List[String] = resultMap.toList.sortBy(_._2).take(10).map(_._1)

      //写出
      sparkContext.makeRDD(result,1).saveAsTextFile(outputPath)

    }

  }
}
