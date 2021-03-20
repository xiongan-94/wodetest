package com.atguigu.chapter05

object $04_FunctionDefind {

  /**
    * 函数的语法:  val 函数名 = (参数名:参数类型,....) => { 函数体 }
    * 函数的返回值就是函数体的块表达式的结果值
    * 函数不可以重载，因为函数名其实就是变量名，同一作用域不能存在同名参数
    *
    * 函数的简化：
    *     1、如果函数体中只有一行语句，{}可以省略
    *
    * 函数在调用的时候必须带上()
    */
  def main(args: Array[String]): Unit = {

    println(add(2, 3))

    //方法就是函数,函数也是对象
    println(add(2,3))
    println(add2(2,3))

    println(add3(2,3))

    println(add4)
  }



  val add = (x:Int,y:Int) => {
    x-y
  }

  val add4 = () => 10 * 10

  val add3 = (x:Int,y:Int) => x+y

  //函数不可以重载，因为函数名其实就是变量名，同一作用域不能存在同名参数
/*  val add = (x:Int) => {
    x-10
  }*/

  val add2 = new Function2[Int,Int,Int] {
    override def apply(v1: Int, v2: Int): Int = v1-v2
  }

  val name = "zhangsan"
}
