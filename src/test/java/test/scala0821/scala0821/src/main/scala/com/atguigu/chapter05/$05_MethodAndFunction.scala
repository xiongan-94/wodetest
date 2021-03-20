package com.atguigu.chapter05

object $05_MethodAndFunction {

  /**
    * 方法和函数的区别:
    *   1、方法如果定义在类里面，可以重载，函数因为是对象，所以不能重载
    *   2、方法是保存在方法区,函数是对象保存在堆里面
    * 联系:
    *   1、如果方法定义在方法里面,它其实就是函数
    *   2、方法可以手动转成函数:   方法名 _
    */
  def main(args: Array[String]): Unit = {

    val f = m1 _
    println(f(2,3))
  }

  /**
    * 方法重载
    */
  def add(x:Int,y:Int):Int = x+y

  def add(x:Int) = x * x

  val func = (x:Int,y:Int) => x+y

  //val func = (x:Int) => x*x

  def m1(x:Int,y:Int) = x+y
}
