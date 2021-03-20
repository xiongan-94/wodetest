package com.atguigu.chapter05

object $08_Map {

  /**
    * 1、定义一个高阶函数，按照指定的规则对集合里面的每个元素进行操作
    * 比如: val arr = Array[String]("spark","hello","java","python")
    * 对集合中每个元素进行操作，得到集合每个元素的长度
    * val result = Array[Int](5,5,4,6)
    */
  def main(args: Array[String]): Unit = {

    val arr = Array[String]("spark","hello","java","python")
    val func = (x:String) => x.charAt(2).toInt

    val result = map(arr,func)
    println(result.toBuffer)
  }

  def map(arr:Array[String],func: String => Int)={

    for(element<- arr) yield {
      func(element)
    }
  }
}
