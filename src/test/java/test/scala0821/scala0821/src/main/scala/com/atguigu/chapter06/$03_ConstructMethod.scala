package com.atguigu.chapter06

import scala.beans.BeanProperty

object $03_ConstructMethod {

  class Person(@BeanProperty val name:String = "zhangsan", var age:Int=20, address:String){

    def this(name:String) {
      //第一行必须调用其他的辅助构造器或者主构造器
      this(name=name,address="beijing")
      //this("lisi",100)
    }

    def this(age:Int) {
      this("zhaoliu")
    }

    def this(name:String,age:Int) {
      this(name=name,address="beijing")
    }
  }
  /**
    *  java中构造方法的定义: 修饰符 类名(参数类型 参数名,..) {..}
    *
    *  scala中构造方法分为两种:
    *     1、主构造器: 定义类名后面{}之前,通过()表示
    *         主构造器中属性的定义:  [修饰符] [val/var] 属性名:类型 = 默认值
    *             柱构造器中val/var以及不写val/var的区别：
    *                 val定义的属性不能更改
    *                 var定义的数可以更改
    *                 使用val/var修饰的非private的属性既可以在class内部使用也可以在class外部使用
    *                 不用val/var修饰的属性只能在class内部使用
    *     2、辅助构造器
    *         辅助构造器定义在class里面
    *         语法: def this(参数名:参数类型,...){
    *           //第一行必须调用其他的辅助构造器或者主构造器
    *           this(..)
    *         }
    *
    */
  def main(args: Array[String]): Unit = {
      val person = new Person("lisi",100,"shenzhen")
      println(person.name)
    println(person.age)
     println(person.getName)

    val person2 = new Person("lisi")
    println(person2.getName)

    val person3 = new Person(100)
    println(person3.getName)



  }
}
