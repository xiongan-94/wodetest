package com.atguigu.chapter05

object $17_Lazy {

  /**
    * 惰性求值: 变量只有在真正使用的时候才会赋值
    * @param args
    */
  def main(args: Array[String]): Unit = {

    lazy val name = "zhangsan"
    val age = 20
    println(name)
    println(age)


  }
}
