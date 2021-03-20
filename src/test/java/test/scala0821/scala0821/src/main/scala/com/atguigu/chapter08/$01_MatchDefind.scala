package com.atguigu.chapter08

import scala.io.StdIn

object $01_MatchDefind {

  /**
    * 语法:   变量 match {
    *
    *      case 值1 => {
    *           .....
    *      }
    *      case 值2 => {
    *        ...
    *      }
    *      .....
    * }
    *
    * 模式匹配可以有返回值，返回值是符合条件的分支的{}的最后一个表达式的结果值
    *
    */
  def main(args: Array[String]): Unit = {

    val word = StdIn.readLine("请输入一个单词:")

    val result = word match {
      case "hadoop" =>
        println("输入的单词是hadoop")
        10

      case "spark" =>
        println("输入的单词是spark")
        20

      case "flume" =>
        println("输入的单词是flume")
        30

        //相当于switch的default
/*      case x => {
        println(s"其他单词${x}")
      }*/
        //如果变量不需要在=>右边使用可以用_代替
      case _ => {
        println(s"其他单词")
        -1
      }
    }

    println(result)
  }
}
