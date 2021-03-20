package com.atguigu.chapter06

object $07_AbstarctClass {

  abstract class Animal{

    //抽象属性
    val name:String
    //具体属性
    var age:Int = 10
    //抽象方法
    def m1(x:Int,y:Int)
    //具体方法
    def add(x:Int,y:Int) = x*y

  }

  class Dog extends Animal {
    //具体方法
    override def m1(x: Int, y: Int): Unit = x+y

    //重写抽象属性
    override val name: String = "lisi"
  }


  /**
    * 抽象类:
    *     1、语法: abstract class 类名{....}
    *     2、scala的抽象类中可以定义抽象方法，也可以定义具体方法
    *        scala中抽象类中可以定义抽象属性，也可以定义具体属性
    *           抽象方法: 没有方法体的方法称之为抽象方法,定义抽象方法的时候如果不定义返回值类型，返回值默认就是Unit
    *           抽象属性: 没有赋予初始值的属性【定义抽象属性的时候，属性的类型必须定义】
    *
    */
  def main(args: Array[String]): Unit = {

    val dog = new Dog
    println(dog.m1(10, 20))
    println(dog.name)
    println(dog.age)

    //匿名子类
    val p = new Animal {
      override val name: String = "zhangsan"

      override def m1(x: Int, y: Int): Unit = println(s"${x} ${y}")
    }

    println(p.name)
    p.m1(10,20)
  }
}
