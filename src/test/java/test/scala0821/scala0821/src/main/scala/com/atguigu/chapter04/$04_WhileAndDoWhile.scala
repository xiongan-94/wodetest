package com.atguigu.chapter04

object $04_WhileAndDoWhile {

  /**
    * scala中while、do-while的用法与java一样
    * while与do-while的区别：
    *   while  先判断在执行
    *   do-while  先执行在判断
    * @param args
    */
  def main(args: Array[String]): Unit = {

    var i = 11

    while ( i<=10 ){
      println(s"i=${i}")
      i=i+1
    }
  println("*"*30)
    var j =11
    do{
      println(s"j=${j}")
      j = j+1
    }while(j<=10)
  }
}
