package com.atguigu.chapter11

object $05_GenericContext {

  /**
    * 泛型上下文语法: def 方法名[T:类型](参数名:T) = {
    *       val 变量名 = implicitly[类型[T]]
    *       ....
    * }
    * @param args
    */
  def main(args: Array[String]): Unit = {

    implicit val p = new Person[String]
    f[String]("zhangsan")

    f[String]("lisi")
  }

  class Person[U]{

    var field:U = _

    def printHello = println("hello.....")

    def setField(field:U) = this.field = field
  }


  def f[A](a:A)(implicit person:Person[A])= {

    person.setField(a)
    person.printHello
  }

  def f1[A:Person](a:A) = {
    val person = implicitly[Person[A]]
    person.setField(a)
    person.printHello
  }
}
