package com.atguigu.chapter02

object $05_DataType {

  /**
    * java数据类型:
    *   1、基本数据类型
    *      byte、short、char、int、long、float、double、boolean
    *   2、引用数据类型
    *     String、class、集合、数组
    *
    * scala数据类型:
    *   Any: 是所有类的父类
    *       AnyVal: 值类型
    *           Byte、Short、Char、Int、Long、Float、Double、Boolean
    *           Unit: 相当于java的void, 有一个实例()
    *           StringOps: 对java String的扩展
    *       AnyRef: 引用类型
    *          String、scala class、java class、scala集合、java集合,..
    *               Null: 是所有引用类型的子类, 有一个实例null[null一般是给引用类型赋初始值使用，在定义变量的时候如果使用null赋初始值，此时变量的类型必须定义]
    *
    *      Nothing: 所有类型的子类,但是不是给程序员使用，是scala内部使用
    *
    * @param args
    */
  def main(args: Array[String]): Unit = {

    /**
      * String str = null
      * str = "zhangsan"
      *
      * class Person
      *
      * class Student extends Person
      *
      * Student s = new Student
      *
      * s = new Person
      */
    var s:String = null

    s = "zhangsan"

    println(s)

    //val a:Int = null

    //println(a)

  }
}
