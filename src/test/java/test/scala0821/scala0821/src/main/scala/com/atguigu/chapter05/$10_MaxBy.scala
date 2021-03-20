package com.atguigu.chapter05

object $10_MaxBy {

  /**
    * 3、定义一个高阶函数，按照指定的规则获取指定最大元素
    * val arr = Array[String]("zhangsan 20 3000","lisi 30 4000","wangwu 15 3500")
    * 获取年龄最大的人的数据
    * val result = "lisi 30 4000"
    *
    */
  def main(args: Array[String]): Unit = {
    val arr = Array[String]("zhangsan 20 3000","lisi 30 4000","wangwu 15 3500")

    val func = (x:String,y:String) => {
      val first = x.split(" ")(2).toInt
      val last = y.split(" ")(2).toInt
      if(first>last) {
        x
      }else{
        y
      }
    }
    println(maxBy1(arr, func))

    println(maxBy2(arr, (x: String) => x.split(" ")(2).toInt))
  }

  def maxBy1( arr:Array[String], func: (String,String)=> String)={

    var tmp = arr(0)

    for(i<- 1 until arr.length){
      tmp = func(tmp,arr(i))
    }

    tmp
  }

  def maxBy2( arr:Array[String],func: String=>Int) = {

    var tmp = func(arr(0))
    var result = arr(0)

    for(element<- arr){
      if(tmp<func(element)){
        tmp = func(element)
        result = element
      }
    }

    result
  }

}
