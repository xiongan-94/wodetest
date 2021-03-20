package com.atguigu.chapter01

/**
  * java中main方法: public static void main(String[] args){...}
  * scala中没有static关键字
  * class: 里面的所有的属性与方法都是非static的
  * object: 里面的所有的属性和方法都是类似java static修饰的。
  */
object WordCount {

  val name:String = "zhangsan"

  /**
    * scala中没有public修饰符,默认就是public效果
    * def: defined的缩写,def标识方法
    * main: 方法名
    * Array[String]: Array代表是数组,[]是泛型使用,[]里面的类型代表当前Array中转载的元素类型
    * Unit: 相当于java 的void
    *
    * scala中如果一行只写一个语句，那么;可以省略
    * 如果一行写多个语句，;用来分割这多个语句
    */
  def main(args: Array[String]): Unit = {

    //java语法
    System.out.println("wordcount")

    //scala
    println("wordcount")


    println(WordCount.name)
  }
}
