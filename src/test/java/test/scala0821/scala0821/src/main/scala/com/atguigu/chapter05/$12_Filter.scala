package com.atguigu.chapter05

object $12_Filter {

  /**
    * 5、定义一个高阶函数，按照指定的规则对数据进行过滤，保留符合要求的数据
    * val arr =Array[Int](10,2,4,6,1,8)
    * 只保留偶数数据
    * val result = Array[Int](10,2,4,6,8)
    */
  def main(args: Array[String]): Unit = {
    val arr =Array[Int](10,2,4,6,1,8)
    println(filter(arr, x => x % 2 == 0).toBuffer)
  }

  def filter( arr: Array[Int], func: Int => Boolean )={

    for(element<- arr if( func(element) )) yield {
       element
    }
  }
}
