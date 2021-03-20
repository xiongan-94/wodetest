package com.atguigu.chapter06

object $09_Trait {

  trait Logger{

    //抽象属性
    val name:String
    //具体属性
    val age:Int = 20
    //抽象方法
    def add(x:Int,y:Int):Int
    //具体方法
    def m1(x:Int) = x * x
  }

  trait  Logger2

  class A

  class WarnLogger extends A with Logger with Logger2 {
    override val name: String = "zhangsan"

    override def add(x: Int, y: Int): Int = x+y
  }
  /**
    * 语法: trait 特质名{....}
    * 在trait中既可以定义抽象方法也可以定义具体方法
    * 既可以定义抽象字段也可以定义具体字段
    *
    * 子类如果需要继承父类，extends关键字用来继承父类，trait的实现通过with关键字来做
    * 子类如果不需要继承父类,extends关键字用来继承第一个trait，后续其他的trait通过with关键字实现
    *
    */
  def main(args: Array[String]): Unit = {

    //匿名子类
    val logger = new Logger {
      override val name: String = "lisi"

      override def add(x: Int, y: Int): Int = x * y
    }

    println(logger.add(10, 20))

    val warn = new WarnLogger
    println(warn.add(100,200))
  }
}
