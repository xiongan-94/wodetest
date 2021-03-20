package com.atguigu.chapter05

object $16_Recursion {

  /**
    * 递归: 自己调用自己称之为递归
    *     前提:
    *         1、必须要有退出条件
    *         2、递归必须定义返回值类型
    */
  def main(args: Array[String]): Unit = {

    println(m1(5))



  }


  def m1(n:Int):Int = {

    if(n==1){
      1
    }else{
      n * m1(n-1)
    }

  }
}
