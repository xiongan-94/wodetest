package com.atguigu.chapter08

import scala.util.Random

object $04_MatchType {

  /**
    * 匹配语法:  变量 match{
    *         case 变量名:类型 => ...
    *         case 变量名:类型 => ..
    *         case _:类型 => ..   //如果变量名不需要在=>右边使用可以用_代替
    *         ...
    *    }
    */
  def main(args: Array[String]): Unit = {

    val list:List[Any] = List(1,2.0,false,10,"spark")

    val index = Random.nextInt(list.length)

    val value = list(index)
    println(value)
    val result  = value match {
      case _:String => "输入的是字符串"
      case x:Int => s"输入的是整数${x}"
      case _:Double => "输入的是浮点型"
      case _:Boolean => "输入的是布尔"
      case _ => "其他"
    }
    println(result)
  }
}
