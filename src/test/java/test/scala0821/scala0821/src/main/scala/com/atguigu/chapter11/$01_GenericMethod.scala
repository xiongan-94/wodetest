package com.atguigu.chapter11

object $01_GenericMethod {

  /**
    * 泛型主要用于方法与类
    * 泛型方法的定义语法: def 方法名[T,U](x:T):U = {..}
    */
  def main(args: Array[String]): Unit = {

    println(m1[Int](Array[Int](1, 2, 3)))
  }

  def m1[T](x:Array[T]):Int = {

    x.length
  }
}
