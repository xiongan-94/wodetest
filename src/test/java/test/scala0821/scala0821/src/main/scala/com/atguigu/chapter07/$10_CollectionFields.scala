package com.atguigu.chapter07

object $10_CollectionFields {

  def main(args: Array[String]): Unit = {

    val list = List[Int](10,20,30)

    //获取集合的长度
    println(list.size)
    println(list.length)

    //是否包含某个元素
    println(list.contains(100))

    //判断集合是否为空
    println(list.isEmpty)

    //判断集合是否不为空
    println(list.nonEmpty)

    //生成字符串
    println(list)
    println(list.mkString("#"))

  }
}
