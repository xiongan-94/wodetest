package com.atguigu.chapter04

object $02_IF_ELSE {

  /**
    * java中条件表达式的用法：
    *   1、单分支: if
    *   2、双分支: if-else
    *   3、多分支： if-else if -..-else
    *
    * scala中条件表达式的用法：
    *    1、单分支: if
    *    2、双分支: if-else
    *    3、多分支： if-else if -..-else
    * scala的条件表达式有返回值,返回值是符合条件的分支的{ }的最后一个表达式的结果值
    */
  def main(args: Array[String]): Unit = {

    val a =10
    //单分支
    if(a%2==0){
      println("a是偶数")
    }

    //双分支
    if(a%3==0){
      println("a是3的倍数")
    }else{
      println("a不是3的倍数")
    }

    //多分支
    if(a%3==0){
      println("a是3的倍数")
    }else if(a%4==0){
      println("a是4的倍数")
    }else{
      println("其他...")
    }

    //if表达式有返回值
    val b = if(a%2==0){
      println("..............")
      a*a
    }
    println(b)

    val c = if(a%10==0){

      if(a%5==0){
        println("5.。。。。。。。。")
        a*a
      }else{
        println("+++++++++++++")
        a+10
      }

    }

    println(c)

  }
}
