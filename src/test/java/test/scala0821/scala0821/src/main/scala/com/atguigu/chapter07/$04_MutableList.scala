package com.atguigu.chapter07

import scala.collection.mutable.ListBuffer

object $04_MutableList {

  /**
    * 1、创建方式: ListBuffer[元素类型](初始元素,....)
    */
  def main(args: Array[String]): Unit = {
    //创建方式: ListBuffer[元素类型](初始元素,....)
      val list = ListBuffer[Int](10,20,30)
    println(list)

    //添加元素
    val list2 = list.+:(40)
    println(list2)

    val list3 = list.:+(50)
    println(list3)

    list.+=(60)
    println(list)

    list.+=:(70)
    println(list)

    val list5 = list.++(Array(80,90,100))
    println(list5)

    val list6 = list.++:(Array(200,300))
    println(list6)

    list.++=(Array(100,200))
    println(list)

    list.++=:(Array(1000,2000))
    println(list)

    list.append(500,600,700)
    println(list)

    //删除元素
    val list8 = list.-(1000)
    println(list8)

    list.-=(2000)
    println(list)

    val list9 = list.--(Array(10,20,30))
    println(list9)

    list.--=(Array(500,600,700))
    println(list)

    list.remove(0)
    println(list)

    //获取元素
    println(list(0))

    //修改
    list(0)=1000
    println(list)

    list.update(1,200)
    println(list)

    val list10 = list.updated(3,500)
    println(list10)
  }
}
