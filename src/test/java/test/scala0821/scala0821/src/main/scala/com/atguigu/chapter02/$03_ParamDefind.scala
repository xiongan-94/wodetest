package com.atguigu.chapter02

object $03_ParamDefind {

  /**
    * java中变量定义:  类型 变量名 = 值
    *
    *
    *
    * scala中变量定义语法:  val/var 变量名:变量类型 =  值
    * val与var的区别:
    *   val定义的变量不可以被重新赋值
    *   var定义的变量可以被重新赋值
    * scala在定义变量的时候，变量类型是可以省略，省略之后scala会自动推断数据类型
    *
    * scala定义变量的时候必须初始化
    * @param args
    */
  def main(args: Array[String]): Unit = {

    val name:String = "zhangsan"
    //val定义的变量不可以被重新赋值
    //name = "lisi"
    var age:Int = 100

    age = 200
    println(name)
    println(age)

    val address = "shenzhen"
    println(address)
  }
}
