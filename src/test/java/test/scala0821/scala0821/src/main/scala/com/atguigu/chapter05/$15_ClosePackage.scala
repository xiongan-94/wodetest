package com.atguigu.chapter05

object $15_ClosePackage {

  /**
    * 闭包： 函数体中使用了不属于自身的变量的函数
    * @param args
    */
  def main(args: Array[String]): Unit = {

    println(func(100))

  }
  val b = 10

  /**
    * 闭包
    */
  val func = (x:Int) => {
    val c = 10
    x + c + b
  }


  def add(x:Int,y:Int) = {

/*    val func = (z:Int)=> {

      val func2 = (a:Int)=> a+x+y+z
      func2
    }

    func*/

    (z:Int)=> {
      (a:Int)=> a+x+y+z
    }
  }
}
