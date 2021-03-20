package com.atguigu

import java.text.SimpleDateFormat

import scala.io.Source

object $01_Home {

  /**
    * 需求: 统计每个用户一小时内的最大登录次数
    * @param args
    */
  def main(args: Array[String]): Unit = {

    //1、读取数据
    val datas = Source.fromFile("datas/datas.txt","utf-8").getLines().toList
    //2、切割数据,转换时间
    val mapDatas = datas.map(line=>{
      val arr = line.split(",")
      val userID = arr.head
      val time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(arr.last).getTime
      (userID,time)
    })

    //3、遍历数据，根据遍历的每条数据筛选哪些数据与该数据处于一个小时
    mapDatas.map(x=>{
      //x = (a,2020-07-11 10:51:12)
      //4、统计每个时间对应的登录次数
      val num = mapDatas.filter(y=>y._1 == x._1 && y._2>=x._2 && y._2<=x._2+3600000).size
      (x._1,num)
    })
    //5、按照用户进行分组
      .groupBy(_._1)

    /**
      * Map(
      *   a-> List( (a,5),(a,6),(a,4),..)
      *   b-> List( (b,4),..)
      * )
      */
      //6、统计最大登录次数
      .map(x=>{
      //x=a-> List( (a,5),(a,6),(a,4),..)
      x._2.maxBy(_._2)
    })
    //7、结果展示
      .foreach(println(_))


  }
}
