package com.atguigu.chapter05

object $13_NoNameFunction {

  /**
    * 匿名函数: 没有函数名的函数
    * 匿名函数不能单独使用,只能将匿名函数作为高阶函数的参数进行传递
    */
  def main(args: Array[String]): Unit = {

    val func = (x:Int,y:Int) => x+y

    println(func(20, 10))

    //(x:Int,y:Int) => x+y

    def add(x:Int,y:Int,func: (Int,Int)=>Int) = {
      func(x,y)
    }

      println(add(10, 20, (x: Int, y: Int) => x + y ))
  }
}
