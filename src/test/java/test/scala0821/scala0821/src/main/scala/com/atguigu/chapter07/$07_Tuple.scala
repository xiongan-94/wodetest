package com.atguigu.chapter07

object $07_Tuple {
 class Person(val name:String,val age:Int,val address:String)

  class School(val name:String,val clazz:Clazz)

  class Clazz(val name:String,val student:Student)

  class Student(val name:String,val age:Int)
  /**
    * 创建方式：
    *     1、通过()方式创建: (初始元素,..)
    *     2、如果是创建二元元组,还可以 K->V 的方式创建
    * 元组一旦创建,长度以及元素都不可改变
    * 元组最多只能存放22个元素
    * 元组的值的获取: 元组的变量._角标 【角标从1开始】
    */
  def main(args: Array[String]): Unit = {

    //1、通过()方式创建: (初始元素,..)
    val t1 = ("zhangsan",20,"beijing")
    //2、如果是创建二元元组,还可以 K->V 的方式创建
    val t2 = "zhangsan" -> 20

    println(t1._1)

    val school = new School("宝安中学",new Clazz("xx",new Student("lisi",20)))

    val school2 = ("宝安中学",("xx",("lisi",20)))

    println(school2._2._2._1)

  }

  def m1(name:String,age:Int,address:String) = {
    (name,age,address)
  }
}
