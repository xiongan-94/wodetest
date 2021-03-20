package com.atguigu.chapter06

object $11_TraitMethod {

  trait ParentLogger{
    def log(msg:String) = println(s"ParentLogger:${msg}")
  }

  trait Logger1 extends ParentLogger{

    override def log(msg:String) = {
      println(s"Logger1:${msg}")

    }
  }

  trait Logger2 extends ParentLogger{
    override def log(msg:String) = {
      println(s"Logger2:${msg}")

    }
  }

  trait Logger3 extends ParentLogger{
    override def log(msg:String) = {
      println(s"Logger3:${msg}")

    }
  }

  class A extends Logger1 with Logger2 with  Logger3{
    override def log(msg: String): Unit ={
      println(s"A:${msg}")
      super[Logger2].log(msg)
    }
  }

  /**
    * scala可以多实现，如果实现的多个trait中有同名方法，子类实现这多个trait之后相当于有多个同名方法
    * 解决方案:
    *   1、在子类中重写父trait的同名方法
    *   2、创建一个新trait，在新trait中创建一个同名方法，子类的父trait全部继承该新trait。
    *       如果父trait中有通过super关键字调用同名方法，此时调用顺序是按照继承顺序从右向左开始调用
    * @param args
    */
  def main(args: Array[String]): Unit = {
    val a = new A

    a.log("xxxx")
  }
}
