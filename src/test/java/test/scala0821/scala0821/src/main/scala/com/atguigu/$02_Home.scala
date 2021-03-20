package com.atguigu

import java.text.SimpleDateFormat
import java.util.UUID

case class UserAnasisy(var sessionid:String,userid:String,time:Long,page:String)

object $02_Home {

  def main(args: Array[String]): Unit = {

    val list = List[(String,String,String)](

      ("1001","2020-09-10 10:21:21","home.html"),
      ("1001","2020-09-10 10:28:10","good_list.html"),
      ("1001","2020-09-10 10:35:05","good_detail.html"),
      ("1001","2020-09-10 10:42:55","cart.html"),
      ("1001","2020-09-10 11:35:21","home.html"),
      ("1001","2020-09-10 11:36:10","cart.html"),
      ("1001","2020-09-10 11:38:12","trade.html"),
      ("1001","2020-09-10 11:40:00","payment.html"),
      ("1002","2020-09-10 09:40:00","home.html"),
      ("1002","2020-09-10 09:41:00","mine.html"),
      ("1002","2020-09-10 09:42:00","favor.html"),
      ("1003","2020-09-10 13:10:00","home.html"),
      ("1003","2020-09-10 13:15:00","search.html")
    )
    // 每条数据都给一个唯一标识，进行数据转换
    list.map(x=>{
      val sessionid = UUID.randomUUID().toString
      val time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(x._2).getTime
      UserAnasisy(sessionid,x._1,time,x._3)
    })
    //分组
      .groupBy(_.userid)

    /**
      * Map(
      *     1001-> List(UserAnasisy("1","1001","2020-09-10 10:21:21","home.html"),UserAnasisy("2","1001","2020-09-10 10:28:10","good_list.html"),UserAnasisy...)
      *     1002-> List(...)
      * )
      */

    //按照时间排序
      .flatMap(x=>{
      val sortedUser = x._2.sortBy(_.time)
      //窗口
      val slidingList = sortedUser.sliding(2)

      /**
        * List(
        *   List(UserAnasisy("1","1001","2020-09-10 10:21:21","home.html"),UserAnasisy("2","1001","2020-09-10 10:28:10","good_list.html"))
        *   List(UserAnasisy("2","1001","2020-09-10 10:28:10","good_list.html")，UserAnasisy ("3","1001","2020-09-10 10:35:05","good_detail.html"))
        *   ...
        *   )
        */
      slidingList.foreach(y=>{
        //y = List(UserAnasisy("1","1001","2020-09-10 10:21:21","home.html"),UserAnasisy("2","1001","2020-09-10 10:28:10","good_list.html"))
        //判断上一次与下一次是否在30分钟内，如果在30分钟内就是属于同一次会话
        if(y.last.time - y.head.time < 30*60*1000)  {
          y.last.sessionid = y.head.sessionid
        }

      })
      x._2
    })

    //结果展示
      .foreach(println(_))

  }

}
