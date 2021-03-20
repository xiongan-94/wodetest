package com.atguigu.chapter07

object $03_ImmutableList {

  /**
    * 1、创建方式:
    *     1、List[元素类型](初始元素,...)
    *     2、元素 :: 元素 :: .. :: list/Nil
    *         :: 最后面必须要是一个List或者是Nil
    *         Nil就是相当于是List的子类
    *
    *     ::与:::的区别：
    *       ::是添加单个元素
    *       ::: 是添加一个集合的所有元素
    */
  def main(args: Array[String]): Unit = {
    //1、List[元素类型](初始元素,...)
      val list = List[Int](10,20,30)
      println(list)
    //元素 :: 元素 :: .. :: list/Nil
    val list2 = 10 :: 20 :: 30 :: Nil
    println(list2)

    var list3:List[Int] = Nil
    list3 = list2
    //2、添加元素
    val list4 = list2.:+(50)
    println(list4)

    val list6 = list2.+:(60)
    println(list6)

    val list7 = list2.++(Array(100,200,300))
    println(list7)

    val list8 = list2.++:(Array(400,500,600))
    println(list8)

    val list9 = 100 :: list8
    println(list9)

    val list10 = list9 ::: list
    println(list10)

    //获取元素
    println(list10(2))

    //修改元素
    //list10(2) = 1000
    val list11 = list10.updated(2,1000)
    println(list11)

    //List转数组
    println(list11.toBuffer)
  }
}
