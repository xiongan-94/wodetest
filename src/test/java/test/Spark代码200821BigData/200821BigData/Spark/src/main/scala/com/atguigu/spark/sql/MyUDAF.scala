package com.atguigu.spark.sql

import java.text.DecimalFormat

import org.apache.spark.sql.{Encoder, Encoders}
import org.apache.spark.sql.expressions.Aggregator

import scala.collection.mutable

/**
 * Created by VULCAN on 2020/12/7
 *
 *    函数：  cityInfo
 *
 *    函数使用的位置： 在join后，在按照 area 和 produce_id分组后调用
 *
 *    分组：
 *      华北 ， 北京  ， 商品1          当前区域点击总次数： 1    北京点击次数：1
 *       华北 ， 北京  ， 商品1         当前区域点击总次数： 2    北京点击次数：2
 *        华北 ， 天津  ， 商品1        当前区域点击总次数： 3    北京点击次数：2  , 天津点击次数：1
 *         华北 ， 济南  ， 商品1       当前区域点击总次数： 4    北京点击次数：2  , 天津点击次数：1 ,济南点击次数：1
 *
 *
 *          华北 ， 北京  ， 商品2
 * *       华北 ， 北京  ， 商品2
 * *        华北 ， 天津  ， 商品2
 * *         华北 ， 济南  ， 商品2
 *
 *  IN:   String 城市名
 *  BUF:  Int 总次数 ， Map[String，Int] 每个城市点击的次数
 *          自定义Bean
 *  OUT:  String
 *
 *
 *     同一个area下，同一个商品的不同城市的点击百分比
 *        北京21.2%，天津13.2%，其他65.6%
 *
 *        概率=  单个城市的点击次数  / 当前区域的总次数
 */
class MyUDAF extends  Aggregator[String,MyBuf,String]{

  //初始化缓存区
  override def zero: MyBuf = MyBuf(0,mutable.Map[String,Int]())

  //
  override def reduce(b: MyBuf, a: String): MyBuf = {
    // 区域总点击量+1
    b.areaCount += 1
    val map: mutable.Map[String, Int] = b.citysCount
    // 城市点击量+1
    map.put(a,map.getOrElse(a,0) + 1)
    b
  }

  //
  override def merge(b1: MyBuf, b2: MyBuf): MyBuf = {

    // 将b2的区域总点击量合并到b1上
    b1.areaCount += b2.areaCount

    val b2Map: mutable.Map[String, Int] = b2.citysCount

    // 将b2的区域总点击量合并到b1上
    for ((cityName,count) <- b2Map) {

     b1.citysCount.put(cityName,b1.citysCount.getOrElse(cityName,0) + count)

    }

    b1


  }

  // 返回结果   北京21.2%，天津13.2%，其他65.6%
  override def finish(reduction: MyBuf): String = {

    //求区域中点击量前2的城市
    val top2: List[(String, Int)] = reduction.citysCount.toList.sortBy(-_._2).take(2)

    // 求其他的点击量
    var otherCount=0
    if (top2.size >=2){

      otherCount=reduction.areaCount - top2(0)._2 - top2(1)._2

    }else{

      otherCount=reduction.areaCount - top2(0)._2
    }

    //拼接字符串
    val format = new DecimalFormat("0.0%")

    var result=""

    for ((city,count) <- top2) {
      result += city+format.format(count.toDouble / reduction.areaCount)+","
    }
    result += "其他"+format.format(otherCount.toDouble / reduction.areaCount)

    result
  }

  override def bufferEncoder: Encoder[MyBuf] = Encoders.product[MyBuf]

  override def outputEncoder: Encoder[String] = Encoders.STRING
}

case class  MyBuf(var areaCount:Int,var citysCount:mutable.Map[String,Int])
