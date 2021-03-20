package com.atguigu.chapter07

object $19_ParCollection {

  def main(args: Array[String]): Unit = {

    val list = List[Int](14,1,6,9,10,20,7,8)

    list.foreach(x=>println(Thread.currentThread().getName))

    println("8"*100)

    list.par.foreach(x=>println(Thread.currentThread().getName))
  }
}
