package com.atguigu

import com.alibaba.fastjson.JSON
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.GetMethod

import scala.io.Source

object $03_Home {

  def getProvinceCityByIp(ip:String):(String,String) = {
    //1、创建HttpClient
    val client = new HttpClient()
    //2、定义请求方式
    val method = new GetMethod(s"https://restapi.amap.com/v3/ip?ip=${ip}&key=f75418e64363b8a96d3565108638c5f1")
    //3、执行请求
    val code = client.executeMethod(method)
    //4、判断请求是否成功
    if(code==200){
      //5、返回结果
      val json = method.getResponseBodyAsString
      val obj = JSON.parseObject(json)
      val province = obj.getString("province")
      val city = obj.getString("city")
      (province,city)
    }
    else{
      ("","")
    }

  }

  def main(args: Array[String]): Unit = {

    //1、读取数据
    Source.fromFile("datas/pmt.json").getLines().toList
      .take(100)
    //2、列裁剪【ip、requestmode、processnode】、去重、过滤
      .map(line=>{
          val jsonobj = JSON.parseObject(line)
          val ip = jsonobj.getString("ip")
          val requestmode = jsonobj.getLong("requestmode")
          val processnode = jsonobj.getLong("processnode")

          //3、根据ip获取省份/城市
          val (province,city) = getProvinceCityByIp(ip)
          (province,city,requestmode,processnode)
    }).filter{
    //4、过滤Ip获取的省份/城市为空的数据
      case (province,city,requestmode,processnode) =>  province!=null && city != null && province!=""  && city != ""
    }
    //5、按照省份、城市分组
      .groupBy{
      case (province,city,requestmode,processnode) => (province,city)
    }
    //6、统计原始请求数
      .map{
      case ((province,city),list) =>
        val num = list.filter{
          case (province,city,requestmode,processnode) =>
            requestmode == 1 && processnode >= 1
        }.size
        (province,city,num)
    }
    //7、结果展示
      .foreach(println)
  }
}
