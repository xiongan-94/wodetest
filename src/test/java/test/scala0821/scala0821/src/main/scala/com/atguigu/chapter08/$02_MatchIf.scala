package com.atguigu.chapter08

import scala.io.StdIn

object $02_MatchIf {

  /**
    * 守卫语法:
    *     变量  match {
    *         case 值 if(布尔表达式) => ...
    *         case 值 if(布尔表达式) => ...
    *     }
    */
  def main(args: Array[String]): Unit = {

    val line = StdIn.readLine("请输入一句话:")

    line match  {
      case x if(x.contains("hadoop")) => println(s"输入的句子中包含hadoop")
      case x if(x.contains("spark")) => println(s"输入的句子中包含spark")
      case x if(x.contains("flume")) => println(s"输入的句子中包含flume")
      case x => println("其他句子")
    }

  }
}
