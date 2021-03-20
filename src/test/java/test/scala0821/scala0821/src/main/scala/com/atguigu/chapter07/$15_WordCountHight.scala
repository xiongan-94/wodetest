package com.atguigu.chapter07

import scala.io.Source

object $15_WordCountHight {

  def main(args: Array[String]): Unit = {

    val datas = List(("Hello Scala Spark World", 4), ("Hello Scala Spark", 3), ("Hello Scala", 2), ("Hello", 1))

    //1、切割句子获得单词，赋予初始次数，压平
   val words =  datas.flatMap(x=>{
      //x = ("Hello Scala Spark World", 4)
      val arr = x._1.split(" ")
      //Array(Hello,Scala,Spark,World)
      val result = arr.map(y=>(y,x._2))
      //Array( (Hello,4),(Scala,4),(Spark,4),(World,4) )
      result
    })
    //List( (Hello,4),(Scala,4),(Spark,4),(World,4),(Hello,3),(Scala,3),(Spark,3),(Hello,2),(Scala,2),(Hello,1) )

    //2、按照单词分组
    val grouped = words.groupBy(_._1)
    /**
      * Map[
      *     Hello -> List( (Hello,4),(Hello,3),(Hello,2),(Hello,1) ),
      *     Scala -> List( (Scala,4),(Scala,3),(Scala,2)),
      *     Spark-> List( (Spark,4),(Spark,3)),
      *     World -> List( (World,4))
      * ]
      */
    //3、统计单词的总个数
    val result = grouped.map(x=>{
      //x = Hello -> List( (Hello,4),(Hello,3),(Hello,2),(Hello,1) )
      //val num = x._2.map(_._2).sum
      // (x._1,num)
      val result = x._2.reduce((agg,curr)=>(agg._1,agg._2+curr._2 ))
     result
    })
    //List[Hello->10,Scala->9,Spark->7,World->4]
    //4、结果展示
    result.foreach(println)
  }
}
