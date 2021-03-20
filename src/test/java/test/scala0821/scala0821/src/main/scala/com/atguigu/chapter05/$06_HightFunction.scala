package com.atguigu.chapter05

object $06_HightFunction {

  /**
    * 高阶函数:  以函数作为参数或者返回值的方法/函数称之为高阶函数
    *
    *
    */
  def main(args: Array[String]): Unit = {


    val func = (x:Int,y:Int) => x+y

    println(add(10, 20, func))

    //方法就是函数
    //println(add(3,5,m1 _))
    println(add(3,5,m1))
  }

  /**
    * 高阶函数
    * @param x
    * @param y
    * @param func  函数对象，封装的操作
    * @return
    */
  def add(x:Int,y:Int,func: (Int,Int)=>Int ) = {

    func(x,y)
  }

  def m1(x:Int,y:Int) = x*y
}
