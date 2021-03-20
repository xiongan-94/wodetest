package com.atguigu.chapter05
import scala.util.control.Breaks._
object $18_ControlAbstract {

  /**
    *  控制抽象: 其实就是一个块表达式,可以将块表达式作为参数[ => 块表达式的结果值类型 ]传递给方法，后续当做函数进行调用
    *
    *  控制抽象只能用于方法的参数
    *  控制抽象在调用的时候不能带上()
    * @param args
    */
  def main(args: Array[String]): Unit = {

    val b = {
      println("xxxxxxxxxxxx")
      val c = 20
      c * c
    }

    def m1(x:Int) = {

      println(x*x)
    }

    m1({
      println("xxxxxxxxxxxx")
      val c = 20
      c * c
    })


    def m2(x:Int,func: Int=>Int) = {
      func(x)
      func(x)
      func(x)
    }

    m2(10,x=>{
      println(".....................")
      x+10
    })


    var i = 0

    breakable({
      while (i<=10){
        if(i==5) break()
        println(s"i=${i}")
        i=i+1
      }
    })

    val func2 = () => println("hello.........")

    func2()

    m4({
      println("+++++++")
      false
    })
    println("*"*100)
    var j = 0


    myLoop({
      j<=10
    })({
      println(s"j=${j}")
      j = j+1
    })
  }


  def m4(f1: => Unit) = {
    //在调用控制抽象的时候不能带上()
    f1
    f1
    f1
  }

  def myLoop(condition: =>Boolean)(body: =>Unit):Unit = {
    if(condition) {
      body
      myLoop(condition)(body)
    }
  }
}
