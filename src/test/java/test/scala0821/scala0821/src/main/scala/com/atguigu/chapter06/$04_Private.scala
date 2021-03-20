package com.atguigu.chapter06

import scala.beans.BeanProperty

object $04_Private {

  class Person{

    @BeanProperty
    val name:String = "zhangsan"

    //def getName() = this.name


  }

  /**
    * 封装: 属性私有,提供公有的set/get方法
    *
    * scala中属性一般不私有，通过@BeanProperty注解提供公有的set/get方法
    * @BeanProperty对于val修饰的属性只提供get方法
    *                                对于var修饰的属性提供set与get方法
    */
  def main(args: Array[String]): Unit = {

   // println($06_Package.AAAAAAA)
    val person = new Person

    println(person.getName)
  }
}
