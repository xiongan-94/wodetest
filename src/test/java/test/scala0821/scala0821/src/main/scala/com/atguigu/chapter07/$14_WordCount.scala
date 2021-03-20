package com.atguigu.chapter07

object $14_WordCount {


  def main(args: Array[String]): Unit = {

    val datas = List("Hello Scala Hbase kafka", "Hello Scala Hbase", "Hello Scala", "Hello")

    //1、切割句子获取单词，压平
    //datas.flatMap(x=>x.split(" "))
    val words = datas.flatMap(_.split(" "))
    //List(Hello,Scala,Hbase,kafka,Hello,Scala,Hbase,Hello,Scala,Hello)
    //2、按照单词进行分组
    val grouped = words.groupBy(x=>x)
    /**
      * Map[
      *     Hello -> List[Hello,Hello,Hello,Hello],
      *     Scala-> List[Scala,Scala,Scala],
      *     Hbase-> List[Hbase,Hbase],
      *     kafka->List[kafka]
      * ]
      */
    //3、统计单词的个数
    val result = grouped.map(x=>{
      //x = Hello -> List[Hello,Hello,Hello,Hello]
      val num = x._2.length
      (x._1,num)
    })
    //4、结果展示
    result.foreach(println)
    //最终结果: [(Hello,4),(Scala,3),(Hbase,2),(Kafka,1)]

   // datas.flatMap(_.split(" ")).groupBy(x=>x).map(x=>(x._1,x._2.size)).foreach(println)
  }
}
