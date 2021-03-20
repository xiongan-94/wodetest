package com.atguigu.chapter07

object $01_ImmutableArray {

  /**
    * 1、不可变数组的创建方式：
    *     1、Array[元素类型](初始元素,..)
    *     2、new Array[元素的类型](数组的长度)
    *
    * 2、插入数据
    * 3、删除数据
    * 4、获取数据
    * 5、修改数据
    *
    * 一个+与两个+的区别:
    *     一个+ 是添加单个元素
    *     两个+ 是添加一个集合的所有元素
    * 冒号在前与冒号在后的区别:
    *   冒号在前 是将元素添加在集合的最末尾
    *   冒号在后 是将元素添加在集合的最前面
    *   不带冒号 是将元素添加在集合的最末尾
    */
  def main(args: Array[String]): Unit = {

    //Array[元素类型](初始元素,..)
    val arr = Array[Int](10,2,4,6,1)
    println(arr.toBuffer)
    //new Array[元素的类型](数组的长度)
    val arr2 = new Array[Int](10)
    println(arr2.toBuffer)

    //添加数据
    val arr3 = arr.+:(20)
    println(arr3.toBuffer)
    println(arr.toBuffer)

    val arr4 = arr.:+(30)
    println(arr4.toBuffer)

    //添加一个集合所有元素
    val arr5 = arr.++(Array(100,200,300))
    println(arr5.toBuffer)

    val arr6 = arr.++:(Array(400,500,600))
    println(arr6.toBuffer)

    //获取元素
    val element = arr6(2)
    println(element)

    //修改元素
    arr6(2)=1000

    //将不可变数组转可变数组
    println(arr6.toBuffer)

    for(i<- arr){
      println(i)
    }
  }
}
