package com.atguigu.chapter04

object $01_Block {

  /**
    * 块表达式: 由{ }包裹的一块代码称之为块表达式
    * 块表达式有返回值，返回值是{ }中最后一个表达式的结果值
    * 后续大家看到{ }就可以认为是块表达式[ for循环、while循环的大括号不能看做块表达式]
    *
    */
  def main(args: Array[String]): Unit = {

    val b = {
      println("hello....")
      val c = 10+10
      //return "xx"
    }
    println(b)
    println("----------------------")
    10
    //return "xx"
  }
}
