package com.atguigu.chapter06

import scala.beans.BeanProperty

object $05_Extends {


  /*final */class Animal{

    /*private */
    @BeanProperty val name:String = "zhangsan"

    @BeanProperty var age:Int = _

    def printHello() = println("...........")
  }

  class Dog extends Animal{

    override val name = "lisi"

    override def printHello(): Unit = {
      println("888888888888888888888888")
      super.printHello()
    }
  }

  /**
    * scala中通过extends关键字来实现继承关系
    *哪些情况不能被继承:
    *   1、private修饰的属性/方法/函数不能被继承
    *   2、final修饰的class不能被继承
    *子类可以通过override关键字来重写父类的方法/val修饰的属性
    *
    * var修饰的属性不能通过override来重写
    * 子类中可以通过super关键字来调用父类的方法
    *
    * java中的多态只是方法的多态，属性不多态
    * scala中方法和属性都是多态的
    */
  def main(args: Array[String]): Unit = {

    val dog = new Dog
    println(dog.name)
    println(dog.age)
    dog.printHello()

    val animal:Animal = new Dog
    animal.printHello()

    println(animal.getName)

    println(CCCCCCCCCC)
  }
}
