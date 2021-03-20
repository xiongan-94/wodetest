package com.atguigu.chapter08

object $09_MatchParamDefind {

  def main(args: Array[String]): Unit = {

    val t1 = ("zhangsan",20,"shenzhen")
    println(t1._1)

    val (name,age,address) = ("zhangsan",20,"shenzhen")
    println(name)

    val x :: Nil = List(10)
    println(x)

    val Array(a,y,z) = Array(10,20,30)
    println(a,y,z)

    val name1:String = "zhangsan"
    println(name)

    val list = List[(String,Int)](("zhangsan",20),("lisi",30))

    for((name,age)<- list){
      println(name)
    }

  }
}
