package com.atguigu.chapter07

import scala.collection.mutable.ArrayBuffer

object $02_MutableArray {

  /**
    * 1、可变数组的创建：
    *       1、ArrayBuffer[元素类型](初始元素,...)
    *       2、new ArrayBuffer[元素类型]()
    * 2、添加元素
    * 3、删除元素
    * 4、修改元素
    * 5、获取元素
    * 集合方法的区别:
    *     一个+/-与两个+/-的区别:
    *         一个+/-是添加单个元素
    *         两个+/-是添加一个集合的所有元素
    *     冒号在前与冒号在后以及不带冒号的区别:
    *         冒号在前是将元素添加在集合的末尾
    *         冒号在后是将元素添加在集合的最前面
    *         不带冒号是将元素添加在集合的末尾
    *     带=与不带=的区别:
    *        带=是修改集合本身
    *        不带=是生成新集合，原有集合没有改变
    *     +与-的区别:
    *       +是指添加元素
    *       -是指删除元素
    */
  def main(args: Array[String]): Unit = {
    //1、ArrayBuffer[元素类型](初始元素,...)
    val arr = ArrayBuffer[Int](2,5,7,1,3)
    println(arr)
    //2、new ArrayBuffer[元素类型]()
    val arr2 = new ArrayBuffer[Int]()
    println(arr2)
    
    //添加元素
    val arr3 = arr.+:(10)
    println(arr3)
    println(arr)

    arr.+=(20)
    println(arr)

    arr.+=:(30)
    println(arr)

    val arr4 = arr.++(Array(100,200))
    println(arr4)

    val arr5 = arr.++:(Array(300,400))
    println(arr5)

    arr.++=(Array(1000,3000))
    println(arr)

    arr.++=:(Array(400))
    println(arr)

    arr.+=:(500)
    arr.+=:(400)
    println("*"*100)
    println(arr)
    //删除元素
    val arr6 = arr.-(400)
    println(arr6)

    arr.-=(1000)
    println(arr)

    val arr7 = arr.--(Array(400,500,30))
    println(arr7)

    arr.--=(Array(2,5,7,50))
    println(arr)

    //获取元素
    println(arr(2))

    //修改元素
    arr(2)=50
    println(arr)

    //可变数组转不可变数组
    println(arr.toArray)


    //多维数组
    val array = Array.ofDim[Int](3,4)

    println(array.length)
    println(array(0).length)
  }
}
