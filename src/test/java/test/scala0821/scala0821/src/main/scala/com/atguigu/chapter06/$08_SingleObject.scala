package com.atguigu.chapter06

object $08_SingleObject {

  class Person(val age:Int){
    private val name = "艾尔"

    def this() = {
      this(10)
    }
    def xx = Person.printHello()
  }

  object Person{
    val age = 100
    private def printHello() = {
      val person = new Person(20)
      println(person.name)
    }

    def apply(age:Int) = new Person(age)

    def apply() = new Person
  }

  //val name = "xxx"
  /**
    * 单例对象的语法:  object xx{...}
    *
    * object中所有的方法/属性/函数都是类似java static修饰的。可以通过 object名称.属性/方法/函数 的方法调用
    *
    * 伴生类和伴生对象
    *     1、class与object的名称要一致
    *     2、class与object要在同一个.scala文件中
    *
    * 伴生类和伴生对象可以互相访问对方private修饰的属性/方法/函数
    *
    * apply方法: 主要用来简化伴生类的对象创建
    *   后续可以通过 object名称.apply(..) / object名称(..) 创建伴生类的对象
    * @param args
    */
  def main(args: Array[String]): Unit = {

    val obj1 = $08_SingleObject
    val obj2 = $08_SingleObject

    println(obj1)
    println(obj2)

    //println($08_SingleObject.name)

    val person = new Person(20)
    person.xx

    val person4 = Person.apply(10)
    person4.xx

    val person5 = Person(10)
    person5.xx

    val arr = Array[Int](10,20,30,40)

    val p = Person()

  }
}


