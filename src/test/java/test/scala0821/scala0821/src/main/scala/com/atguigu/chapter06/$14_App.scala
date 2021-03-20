package com.atguigu.chapter06
// 应用类
object Test20 extends App {

  println("xxxxxxxxxxx");

}

object $14_App {

  // 枚举类
  object Color extends Enumeration {
    val RED = Value(1, "red")
    val YELLOW = Value(2, "yellow")
    val BLUE = Value(3, "blue")
  }



  def main(args: Array[String]): Unit = {

    println(Color.RED)
  }
}
