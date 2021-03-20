package com.atguigu.chapter10

import java.io.File

import scala.io.{BufferedSource, Source}

object xx{


  implicit def fileToSourceBufferd(file:File):BufferedSource = {
    Source.fromFile(file,"utf-8")
  }


  implicit def doubeToInt1(x:Double):Int = x.toInt

  implicit def doubeToInt2(x:Double):Int = x.toInt + 10

}



object $01_ImplicitMethod{

  /**
    * 隐式方法: 悄悄将一个类型转成另一个类型
    *   语法:  implicit def 方法名(变量名:待转换类型): 目标类型 = {..}
    *隐式转换方法使用时机:
    *     1、当前类型与目标类型不一致的时候，会自动调用隐式方法
    *     2、当对象使用了不属于自身的属性/方法/函数的时候,会自动调用隐式方法
    *隐式转换的解析:
    *     当需要使用隐式转换的时候，后首先从当前的作用域中查找是否有符合条件的隐式转换
    *     如果说隐式转换不在当前作用域，需要进行导入之后再使用:
    *       1、如果隐式转换定义在Object中,导入的时候使用: import 包名.object名称.隐式转换方法名
    *       2、如果隐式转换定义在class中,导入的时候使用:
    *           1、创建对象
    *           2、对象名.隐式转换方法名
    * @param args
    */
  def main(args: Array[String]): Unit = {

    // 1、当前类型与目标类型不一致的时候，会自动调用隐式方法
    //val abc = new xx

    import xx.doubeToInt1
    val a:Int = 2.0
    println(a)

    val file = new File("d:/pmt.json")

    //2、当对象使用了不属于自身的属性/方法/函数的时候,会自动调用隐式方法
    import xx.fileToSourceBufferd
    file.getLines()

  }


}
