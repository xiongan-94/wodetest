package com.atguigu.chapter07

object $11_Collection {

  def main(args: Array[String]): Unit = {

    //去重  *****
    val list = List[Int](10,10,20,30,2,1,1,50)
    val list2 = list.distinct
    println(list2)

    //删除前多少个元素，保留剩余的元素
    val list3 = list.drop(2)
    println(list3)

    //删除后多少个元素，保留剩余元素
    val list4 = list.dropRight(3)
    println(list4)

    //获取第一个元素  ****
    val head = list.head
    println(head)

    //获取最后一个元素 *****
    val last = list.last
    println(last)

    //获取除开最后一个元素的所有元素
    val list5 = list.init
    println(list5)
    println("*"*100)
    //除开第一个元素的所有元素
    val list11 = list.tail
    println(list11)

    //反转
    val list7 = list.reverse
    println(list7)

    //获取子集合
    val list8 = list.slice(2,5)
    println(list8)

    //窗口  ******
    //size: 窗口的大小
    //step: 窗口滑动的长度
    val list9 = list.sliding(3,2)
    println(list9.toBuffer)

    //获取前多少个元素 *****
    val list12 = list.take(3)
    println(list12)

    //获取后多少个元素
    val list13 = list.takeRight(3)
    println(list13)

    //val list = List[Int](10,10,20,30,2,1,1,50)
    val list14 = List[Int](10,1,4,5,20)
    //交集[两个集合共同的元素]
    val list15 = list.intersect(list14)
    println(list15)
    //差集[ 除开交集之外的元素  A 差 B => 结果是A中除开交集之外的所有元素 ]
    val list16 = list.diff(list14)
    println(list16)
    //并集【两个集合的所有元素,不去重】
    val list17 = list.union(list14)
    println(list17)

    //拉链
    val list18 = List[String]("aa","bb","cc","dd")
    val list19 = List[Int](1,2,3)
    val list20 = list18.zip(list19)
    println(list20)

    //反拉链
    val list21 = list20.unzip
    println(list21)

  }
}
