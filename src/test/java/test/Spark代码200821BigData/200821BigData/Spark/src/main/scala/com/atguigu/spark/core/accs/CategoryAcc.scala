package com.atguigu.spark.core.accs

import com.atguigu.spark.core.beans.AccBean
import org.apache.spark.util.AccumulatorV2

import scala.collection.mutable

/**
 * Created by VULCAN on 2020/12/4
 *
 *
 *   累加的是 ： 每个品类 点击数，下单数，支付数
 *
 *  IN:   (品类，flag)
 *            0，cc:  点击数
 *            1, oc： 下单数
 *            2, pc： 支付数
 *
 *  OUT: Map[品类, (oc,cc,pc,) / AccBean]
 */
class CategoryAcc extends  AccumulatorV2[(String,String),mutable.Map[String,AccBean]]{

  private var result:mutable.Map[String,AccBean]=mutable.Map[String,AccBean]()

  override def isZero: Boolean = result.isEmpty

  override def copy(): AccumulatorV2[(String, String), mutable.Map[String, AccBean]] = new CategoryAcc

  override def reset(): Unit = result.clear()

  override def add(v: (String, String)): Unit = {

    val bean: AccBean = result.getOrElse(v._1, AccBean(0, 0, 0))

    //更新 bean
    v._2 match {
      case "cc" => bean.cc += 1
      case "oc" => bean.oc += 1
      case "pc" => bean.pc += 1
    }

    //放入map
    result.put(v._1,bean)

  }

  override def merge(other: AccumulatorV2[(String, String), mutable.Map[String, AccBean]]): Unit = {

    val toMergeMap: mutable.Map[String, AccBean] = other.value

    for ((category,bean1) <- toMergeMap) {

      val bean2: AccBean = result.getOrElse(category, AccBean(0, 0, 0))

      bean2.oc += bean1.oc
      bean2.cc += bean1.cc
      bean2.pc += bean1.pc

      result.put(category,bean2)
    }

  }

  override def value: mutable.Map[String, AccBean] = result
}
