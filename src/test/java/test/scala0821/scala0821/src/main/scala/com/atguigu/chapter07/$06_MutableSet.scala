package com.atguigu.chapter07

import scala.collection.mutable

object $06_MutableSet {

  def main(args: Array[String]): Unit = {

    //创建方式: mutable.Set[元素类型](初始元素,...)
    val set = mutable.Set[Int](10,2,6,1,3)
    println(set)

    //添加元素
    val set2 = set.+(10)

    set.+=(20)

    println(set2)
    println(set)

    val set3 = set.++(Array(100,200))
    val set4 = set.++:(Array(100,200))
    set.++=(Array(300,400))
    println(set3)
    println(set4)
    println(set)

    set.add(1000)
    println(set)

    //删除元素
    val set5 = set.-(300)
    set.-=(400)
    println(set5)
    println(set)

    val set6 = set.--(Array(300,2,20))
    set.--=(Array(10,2,1,6))
    println(set6)
    println(set)

    set.remove(1000)
    println(set)

    //set.update()



  }
}
