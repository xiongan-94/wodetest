package com.atguigu.chapter07

object $08_ImmutableMap {

  /**
    * 创建方式:
    *   1、Map[K的类型,V的类型]( (K,V),(K,V),... )
    *   2、Map[K的类型,V的类型] ( K -> V ,...)
    * Map中不能有重复key
    * Option: 提醒外部当前可能返回数据为空需要进行处理
    *   Some: 表示非空,数据封装在Some中,后续可以通过get方法获取数据
    *   None: 表示为空
    */
  def main(args: Array[String]): Unit = {

    val map = Map[String,Int]( ("zhangsan",20),("lisi",30),("zhangsan",40) )

    val map2 = Map[String,Int]( "zhangsan"->20,"lisi"->30)

    println(map)
    println(map2)

    //添加元素
    val map3 = map.+("wangwu"->30)

    val map4 = map.++( Map(("aa",1),("bb",2)) )
    println(map3)
    println(map4)

    val map5 = map.++:(Map(("aa",1),("bb",2)))
    println(map5)

    println(map5.getClass)

    //更新
    val map6 = map5.updated("cc",100)
    println(map6)

    //获取key对应的value数据
    //println(map6("dd"))
    //println(map6.get("dd").get)
    println(map6.getOrElse("dd", 1000))

    //获取map所有的key
    for(key<- map6.keys){
      println(key)
    }

    //获取所有的value
    for(value<- map6.values){
      println(value)
    }


  }
}
