package com.atguigu.chapter05

object $01_MethodDefind {

  /**
    * 方法语法: def 方法名( 变量名:类型,... ) : 返回值类型 = {  方法体 }
    *
    * scala方法可以定义在方法中
    *
    * 方法如果定义在类中是可以重载的
    *
    * 方法如果定义在方法中是不可以重载的
    */
  def main(args: Array[String]): Unit = {

    val result = add(2,3)
    println(result)

    //scala方法可以定义在方法中
    def add2( x:Int,y:Int ) = { x* y }

    //def add2( x:Int ) = { x* 10 }

    println(add2(4, 5))
  }

  //方法如果定义在类中是可以重载的
  def add( x:Int , y:Int ) = {
    x + y
  }

  def add( x:Int ) = {
    x * 10
  }
}
