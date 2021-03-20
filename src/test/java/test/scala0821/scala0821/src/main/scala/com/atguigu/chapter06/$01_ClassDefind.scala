package com.atguigu.chapter06

object $01_ClassDefind {

  //如果class中没有任何东西,{}可以省略
  class Person

  /**
    * java class创建语法: 修饰符 class 名称{...}
    *
    * scala里面没有public关键字,默认就是public
    * scala创建class的语法: class 名称 {...}
    * scala创建对象: new class名称(..)
    */
  def main(args: Array[String]): Unit = {
    //如果使用无参的构造器,()可以省略
    val person = new Person
    println(person)
  }
}
