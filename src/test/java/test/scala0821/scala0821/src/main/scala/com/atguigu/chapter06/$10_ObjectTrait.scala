package com.atguigu.chapter06

object $10_ObjectTrait {

  trait Logger{

    def add(x:Int,y:Int) = x+y
  }

  class Warnlogger

  /**
    * 对象混入: 只让某个对象拥有特质的属性和方法
    *     语法: new class名称 with 特质名
    * @param args
    */
  def main(args: Array[String]): Unit = {

    val warnlogger = new Warnlogger with Logger

    println(warnlogger.add(10, 20))

    val a = new Warnlogger
    //a.add()

  }
}
