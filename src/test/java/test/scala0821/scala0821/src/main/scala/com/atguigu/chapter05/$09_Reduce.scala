package com.atguigu.chapter05

object $09_Reduce {

  /**
    * 2、定义一个高阶函数，按照指定的规则对集合中的所有元素进行聚合
    * val arr =Array[Int](10,2,4,6,1,8,10)
    * 求得集合中的所有元素的和
    * val result = xx
    */
  def main(args: Array[String]): Unit = {
    val arr =Array[Int](10,2,4,6,1,8,10)
    val func = (agg:Int,curr:Int)=> agg * curr
    println(reduce(arr, func))
    //1、将函数作为参数直接传递
    println(reduce(arr, (agg:Int,curr:Int)=> agg * curr))
    //2、函数的参数类型可以省略
    println(reduce(arr, (agg,curr)=> agg * curr))
    //3、函数的参数在函数体中只使用了一次，可以用_代替
    println(reduce(arr, _ * _))
  }

  def reduce( arr:Array[Int],func: (Int,Int)=>Int )={

    var tmp:Int = arr(0)

    for(i<- 1 until arr.length) {
      tmp = func(tmp,arr(i))
    }

    tmp
  }
}
