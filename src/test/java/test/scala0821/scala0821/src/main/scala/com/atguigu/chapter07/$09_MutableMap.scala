package com.atguigu.chapter07

import scala.collection.mutable


object $09_MutableMap {

  /**
    * 创建方式:
    *     1、mutable.Map[K的类型,V的类型]( (K,V),(K,V),.. )
    *     2、mutable.Map[K的类型,V的类型]( K->V,.. )
    * @param args
    */
  def main(args: Array[String]): Unit = {

    val map = mutable.Map[String,Int]( ("zhangsan",20),("lisi",30) )

   val map2 = mutable.Map[String,Int]( "zhangan"->30,"lisi"->20)

    println(map)
    println(map2)

    //添加元素
    val map3 = map.+( "wangwu"->25 )

    map.+=( "aa"->10)
    println(map3)
    println(map)

    val map4 = map.++(Map[String,Int]("cc"->1,"dd"->2))

    val map5 = map.++:(Map[String,Int]("ee"->10,"ff"->20,"aa"->30))

    println(map4)
    println(map5)

    map.++=(Map[String,Int]("pp"->100,"ww"->200))
    println(map)

    map.put("tt",1000)
    println(map)

    //删除元素
    val map6 = map.-("aa")
    println(map6)

    map.-=("ww")
    println(map)

    val map7 = map.--(List("lisi","tt"))
    println(map7)

    map.--=(List("zhangsan","pp"))
    println(map)

    map.remove("lisi")
    println(map)

    //获取元素
    println(map.getOrElse("ddd",-1))

    //获取所有key
    for(key<- map.keys){
      println(key)
    }

    //获取所有的vlaue
    for(value<- map.values){
      println(value)
    }

    //修改元素
    map("aa")=1000
    println(map)

    map.update("tt",100)
    println(map)

    val map10 = map.updated("tt",1)
    println(map10)
    println(map)

  }
}
