package com.atguigu.chapter05

object $14_Currying {

  /**
    * 柯里化: 有多个参数列表的方法称之为柯里化
    * @param args
    */
  def main(args: Array[String]): Unit = {

    println(add(10, 20,30))

    println(add2(10, 20)(30))

    val func = add3(10,20)
    println(func(30))

    println(add3(10, 20)(30))

    println(add4(10,20)(30))

    val func2 = add2 _
    val func3 = add3 _
  }

  def add(x:Int,y:Int,z:Int) = x+y+z

  //柯里化
  def add2(x:Int,y:Int)(z:Int) = x+y+z


  /**
    * 柯里化演变过程
    */
  def add3(x:Int,y:Int) = {
    val func = (z:Int) => x+y+z
    func
  }


  def add4(x:Int,y:Int) = {
    // Person p = new Person
    // return new Person;
    //val func = (z:Int) => x+y+z
    //func
    (z:Int) => x+y+z
  }
}
