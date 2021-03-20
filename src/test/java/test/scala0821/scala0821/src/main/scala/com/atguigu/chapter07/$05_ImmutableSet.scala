package com.atguigu.chapter07

object $05_ImmutableSet {

  /**
    * set的特点: 无序、不重复
    * 创建方式: Set[元素类型](初始元素,...)
    */
  def main(args: Array[String]): Unit = {

    val set = Set[Int](1,5,5,2,10,3)
    println(set)

    //添加元素
    val set2 = set.+(20)
    println(set2)

    val set3 = set.++(Array(100,500,200))

    val set4 = set.++:(Array(100,500,200))
    println(set3)
    println(set4)

    //删除元素
    val set5 = set4.-(500)
    println(set5)

    val set6 = set4.--(Array(2,10,100))
    println(set6)

    //获取元素


    //修改元素

    for(i<- set4){
      println(i)
    }
  }
}
