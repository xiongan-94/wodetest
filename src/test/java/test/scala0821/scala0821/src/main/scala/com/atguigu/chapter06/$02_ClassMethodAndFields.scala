package com.atguigu.chapter06

import com.alibaba.fastjson.JSON

import scala.beans.BeanProperty

object $02_ClassMethodAndFields {

  class Person{
    //定义属性: 修饰符 val/var 属性名:类型 = 值
    //val定义的属性不可被修改
    //var定义的属性可以被修改
    //@BeanProperty注解不能用在private修饰的属性上
    //@BeanProperty
    var name = "zhangsan"
    //@BeanProperty
    var age = 20

   def setName(name:String) = this.name=name

    def getName() = this.name

    def setAge(age:Int) = this.age = age

    def getAge() = this.age


    //scala中属性有默认的get/set方法,get的方法名其实就是属性名，set的方法名其实就是 属性名=

    //在class中var修饰的属性可以使用_初始化,使用_初始化的时候必须指定属性的类型
    var address:String = _
    //setXXx getXXx
    //JSON解析
    //定义方法: 修饰符 def 方法名(参数名:参数类型,...):返回值类型 = {方法体}
    def printName () = println(s"name=${name}")

    //定义函数: 修饰符 val 函数名 = (参数名:参数类型,...) => {函数体}
    val func = (x:Int,y:Int) => x+y
  }

  def main(args: Array[String]): Unit = {

    val person = new Person

    println(person.name)
    println(person.age)
    println(person.address)
    person.age=(100)
    println(person.age)
    person.printName()

    println(person.func(10, 20))
    println("*"*100)
    val json = """{"name":"lisi","age":100}"""
    //json转对象
    val person2 = JSON.parseObject(json,classOf[Person])
    println(person2.name)
    println(person2.age)

  }
}
