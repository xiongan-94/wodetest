package com.atguigu.chapter10

object $03_ImplicitClass {

  class Person(val name:String,val age:Int)

  implicit class MaxMin(p:Person){

    def max(p1:Person) = {
      if(p.age<p1.age) p1
      else p
    }
  }


  /**
    * scala隐式转换类在2.10版本之后才有
    * 语法: implicit class 类名(属性名: 待转换的类型) {
    *       ...
    *   }
    * @param args
    */
  def main(args: Array[String]): Unit = {

    val person1 = new Person("zhagnsan",20)
    val person2 = new Person("lisi",30)

    person1.max(person2)
  }


  //implicit def personToMaxMin(p:Person ):MaxMin = new MaxMin(p)

}
