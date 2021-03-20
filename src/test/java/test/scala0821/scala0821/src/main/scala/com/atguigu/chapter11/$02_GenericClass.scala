package com.atguigu.chapter11

object $02_GenericClass {

  class Person[T,U](var name:T,var age:U) {

    def setName(name:T) = this.name=name

    def getName():T = this.name
  }

  /**
    * 泛型类的定义语法: class 类名[T,U](val/var 属性名:T,..) {
    *
    *       def 方法名(参数名:U):返回值类型 = {..}
    *   }
    * @param args
    */
  def main(args: Array[String]): Unit = {

    val person = new Person[String,Int]("zhangsan",20)

    person.setName("lisi")

    println(person.getName())
  }
}
