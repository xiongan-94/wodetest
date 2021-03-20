package com.atguigu.chapter05

object $07_HightFunctionSample {

  /**
    * 高阶函数的简化：
    *   1、将函数作为值进行传递
    *   2、函数的参数类型可以省略
    *   3、如果函数的参数在函数体中只使用了一次,那么可以用_代替
    *         1、如果在函数体中参数的使用顺序与参数的定义顺序不一致，不能用_代替
    *         2、如果函数的参数只有一个,并且在函数体中没有做任何操作直接将函数的参数返回,此时也不能用_代替
    *   4、如果函数只有一个参数,函数参数列表的()可以省略
    *
    */
  def main(args: Array[String]): Unit = {

    val func = (x:Int,y:Int)=>x*y
    println(add(3, 5, func))
    //1、将函数作为值进行传递
    println(add(3,5, (x:Int,y:Int)=>x*y ))
    //2、函数的参数类型可以省略
    println(add(3,5, (x,y)=>x*y ))
    //3、如果函数的参数在函数体中只使用了一次,那么可以用_代替
    //   前提:
    //      1、如果在函数体中参数的使用顺序与参数的定义顺序不一致，不能用_代替
    //      2、如果函数的参数只有一个,并且在函数体中没有做任何操作直接将函数的参数返回,此时也不能用_代替
    //      3、如果函数体中有嵌套,并且函数的参数处于嵌套中以表达式的方法存在,,此时也不能用_代替
    println(add(3,5, _+_ ))

    //      1、如果在函数体中参数的使用顺序与参数的定义顺序不一致，不能用_代替
    println(add(3,5, (x,y)=>y-x )) //不能用_简化
    //println(add(3,5,_-_))

    val func2 = (x:Int) => x
    println(m1(5, func2))
    println(m1(5, (x:Int) => x))
    println(m1(5, (x) => x))
    //不能用_简化
    val result = m1(5, _)
    //println(m1(5, _))
//
    //println(result(func2))
    //4、如果函数只有一个参数,函数参数列表的()可以省略
    println(m1(5, x => x))


    val func10 = (x:Int) => (x+10)*2
    println(add3(10, x => (x+10)*2))

    println(add3(20,x=>(x)+10))

    println(add3(20,(_)+10))
  }

  def add(x:Int,y:Int,func: (Int,Int)=>Int ) = func(x,y)


  def m1(x:Int, func: Int => Int) = func(x)

  def add3(x:Int,func: Int=>Int) = func(x)
}
