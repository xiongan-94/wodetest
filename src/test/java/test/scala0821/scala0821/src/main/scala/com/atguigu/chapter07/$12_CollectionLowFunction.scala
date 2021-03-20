package com.atguigu.chapter07

object $12_CollectionLowFunction {

  def main(args: Array[String]): Unit = {

    val list = List[Int](2,7,1,9,10,5,3)

    //最大值
    val max = list.max
    //最小值
    val min = list.min
    println(max)
    println(min)
    //根据什么获取最大值
    val list2 = List[String]("zhangsan 20 3000","lisi 15 5000","wangwu 30 1000")
    //maxBy中的函数是针对集合的每个元素
    val maxBy = list2.maxBy(x=>x.split(" ")(1).toInt)
    println(maxBy)
    //根据什么获取最小值
    val list3 = List[(String,Int)]( ("ZHANGSAN",1000),("LISI",2500),("WANGWU",3000),("WANGWU",2500) )
    //minBy中的函数是针对集合的每个元素
    println(list3.minBy(_._2))
    //求和
    println(list.sum)
    //List[String]("spark","hello").sum
    //排序
    //直接按照元素本身排序[升序]
    val list4 = list.sorted

    println(list4)
    //降序
    val list5 = list4.reverse
    println(list5)

    println(list3.sorted)
    //指定按照哪个字段排序[升序] *****
    println(list3.sortBy(_._2))
    //根据指定的规则排序
    println(list.sortWith((x, y) => x < y))
    println(list.sortWith((x, y) => y < x))
  }
}
