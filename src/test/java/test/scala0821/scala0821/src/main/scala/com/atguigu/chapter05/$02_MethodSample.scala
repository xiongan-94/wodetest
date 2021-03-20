package com.atguigu.chapter05

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object $02_MethodSample {

  /**
    * scala方法的简化原则:
    *     1、如果方法体中只有一行语句，{ }可以省略
    *     2、如果方法不需要返回值,那么=可以省略
    *     3、如果使用方法体{}中最后一个表达式的结果值作为方法的返回值,那么此时返回值类型可以省略
    *         如果方法体中有return关键字必须定义返回值类型
    *     4、如果方法不需要参数，那么定义方法的()可以省略
    *           如果定义方法的时候没有(),那么在调用方法的时候也不能带上()
    *           如果定义方法的时候有(),那么在调用方法的时候()可以省略也可以不省略
    *     在简化的时候,=与{ }不能同时省略
    */
  def main(args: Array[String]): Unit = {


    println(add2(2, 3))

    printHello2("lisi")

    //如果定义方法的时候没有(),那么在调用方法的时候也不能带上()
    printName2

    printName()
    printName

  }

  //基本定义方法的语法
  def add(x:Int,y:Int):Int = {
    x+y
  }

  //如果方法体中只有一行语句，{}可以省略
  def add2(x:Int,y:Int):Int = x+y

  //基本语法
  def printHello(name:String):Unit = {
    println(s"hello ${name}")
  }

  //如果方法不需要返回值,那么=可以省略
  def printHello2(name:String) {
    println(s"hello ${name}")
  }

  //基本语法
  def m1(x:Int,y:Int):Int = {
    x+y
  }

  //如果使用方法体{ }中最后一个表达式的结果值作为方法的返回值,那么此时返回值类型可以省略
  def m2(x:Int,y:Int) = {
    x+y
  }
  //如果方法体中有return关键字必须定义返回值类型
  def m3(x:String) = {
/*    if(x==null) return null;
    return "hello";*/
    if(x==null) {
      null
    }else{
      "hello"
    }
  }

  //基本语法
  def printName():Unit = {
    println(".............")
  }

  //如果方法不需要参数，那么定义方法的是()可以省略
  def printName2:Unit = {
    println("............")
  }

  /**
    * 在简化的时候,=与{}不能同时省略
    */
  def printName3 = println("............")

}
