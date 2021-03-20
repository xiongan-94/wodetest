package com.atguigu.chapter05

import java.util

object $11_GroupBy {

  /**
    * 4、定义一个高阶函数，按照指定的规则对数据进行分组
    * val arr = Array[String]("zhangsan 男 深圳","lisi 女 深圳","王五 男 北京")
    * 根据性别进行分组
    * val result = Map[String,List[String]](
    * "男"-> List("zhangsan 男 深圳","王五 男 北京"),
    * "女" -> List("lisi 女 深圳")
    * )
    *
    */
  def main(args: Array[String]): Unit = {
    val arr = Array[String]("zhangsan 男 深圳","lisi 女 深圳","王五 男 北京")

    val func = (x:String) => x.split(" ")(1)
    println(groupBy(arr, func))
    println(groupBy(arr,(x:String) => x.split(" ")(1)))
    println(groupBy(arr,(x) => x.split(" ")(1)))
    println(groupBy(arr,x => x.split(" ")(1)))
    println(groupBy(arr,_.split(" ")(1)))
  }

  def groupBy( arr:Array[String],func: String=>String )={

    //1、创建一个分组的存储容器
    val result = new util.HashMap[String,util.List[String]]()
    //2、遍历集合所有元素
    for(element<- arr){
      //得到分组的key
      val key = func(element)
      //3、判断分组的key是否在容器中存在，如果不存在，则直接添加到容器中
      if(!result.containsKey(key)){
        val list = new util.ArrayList[String]()
        list.add(element)
        result.put(key,list)
      }else{
      //4、如果存在，此时应该取出key对应的List，将元素添加到List
       val list = result.get(key)
        list.add(element)
      }
    }
    //5、返回结果
    result
  }
}
