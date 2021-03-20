package com.atguigu.chapter07

object $13_CollectionHightFunction {

  def main(args: Array[String]): Unit = {

    val list = List[String]("spark","hello","java","python")
    //map(func: 集合元素类型 => B ): 主要用来转换数据[转换值、转换类型]
    //   map中的函数是针对集合的每个元素进行操作
    //   map的应用场景: 一对一 【val B = A.map(..)    B集合的元素的个数=A集合元素个数】
    val list2 = list.map(x=>x.length)
    println(list2)

    val list3 = List[Int](10,20,30,40)
    val list4 = list3.map(x=>x*x)
    println(list4)
    //foreach(func: 集合元素类型 => U ):Unit
    //  foreach中的函数也是针对集合的每个元素
    //  foreach与map的区别: map有返回值，foreach没有返回值
    val list5 = List[(String,Int,String)]( ("lisi",20,"深圳"),("zhangsan",15,"北京") )
    list5.foreach(println(_))
    val func = (x:(String,Int,String)) => println(x)

    list5.foreach(println)

    //flatten-压平
    //flatten只能用于集合中嵌套集合的场景
    //flatten的应用场景: 一对多
    val list6 = List[List[String]](
      List[String]("aa","bb"),
      List[String]("cc","dd"),
      List[String]("ee","ff")
    )

    val list7 = list6.flatten
    println(list7)

    val list8 = List[List[List[String]]](

      List(List[String]("aa","bb"),List[String]("cc","dd")),
      List(List[String]("ee","ff"),List[String]("oo","xx"))
    )

    val list9 = list8.flatten
    println(list9)

    val list10 = List[String]("spark","hello")
    println(list10.flatten)

    //flatMap(func: 集合元素类型=> 集合 ) = map+flatten
    // flatMap中的函数也是针对集合中的每个元素
    // flatMap应用场景: 一对多

    val list11 = List[String]("hadoop spark","hello java python")

    val list12 = list11.map(x=>x.split(" "))

    val list13 = list12.flatten
    println(list13)

    println(list11.flatMap(_.split(" ")))
    //List(hadoop,spark,hello.java,python)

    //filter( func: 集合元素类型 => Boolean ) - 过滤
    //  filter中的函数针对集合的每个元素,filter保留的是函数返回值为true的数据
    val list14 = List[Int](10,2,4,5,7,9)

    println(list14.filter(_ % 2 == 0))

    //groupBy(func: 集合元素类型=> K ) --分组
    //groupBy是根据指定的字段进行分组，里面的函数针对是集合每个元素
    //groupBy的结果是Map[分组的key,List[原有集合元素类型]]
    val list15 = List[(String,String)](
      ("ZHANGSAN","SHENZHEN"),
      ("LISI","BEIJING"),
      ("WANGWU","SHENZHEN"),
      ("ZHAOLIU","SHENZHEN")
    )
    //list15.groupBy(x=> x._2)
    val list16 = list15.groupBy(_._2)
    println(list16)

    //reduce(func: (集合元素类型,集合元素类型) =>  集合元素类型 ) --聚合
    //reduce中的函数是两两聚合，然后将上一次的聚合结果与下一个元素再次聚合
    //reduce最终结果是单个数据，数据的类型就是原有集合的元素类型
    val list17 = List[Int](10,2,4,5,7,9)

    val result = list17.reduce( (agg,curr)=>{
      println(s"agg=${agg}  curr=${curr}")
      agg * curr
    })
    //reduce的执行过程：
      // agg上一次的聚合结果  curr:本次要聚合元素
      //      第一次执行的时候，agg的值就是集合第一个元素[10], curr集合第二个元素[2]  (agg,curr)=> agg * curr = 20
      //      第二次执行的时候，agg上一次的聚合结果[20], curr集合第三个元素[4]  (agg,curr)=> agg * curr = 80
     //  ......

    println(result)

    //reduceRight(func: (集合元素类型,集合元素类型) =>  集合元素类型 )
    //reduceRight中的函数是两两聚合，然后将上一次的聚合结果与下一个元素再次聚合
    //reduceRight最终结果是单个数据，数据的类型就是原有集合的元素类型
    println("*"*100)
    val result2 = list17.reduceRight((curr,agg)=>{
      println(s"agg=${agg}  curr=${curr}")
      agg-curr
    })
    //reduceRight的执行过程：
    // agg上一次的聚合结果  curr:本次要聚合元素
    //      第一次执行的时候，agg的值就是集合第最后一个元素[9], curr集合倒数第二个元素[7]  (agg,curr)=> agg - curr = 2
    //      第二次执行的时候，agg上一次的聚合结果[2], curr集合倒数第三个元素[5]  (agg,curr)=> agg * curr = -1
    //  ......
    println(result2)

    println("+"*100)
    //fold(agg第一次执行的初始值)(func: (集合元素类型，集合元素类型)=>集合元素类型)
    //fold与reduce唯一的不同就是多了agg的初始值
    list17.fold(100)( (agg,curr)=>{
      println(s"agg=${agg}  curr=${curr}")
      agg-curr
    })
    println("-"*100)
    //foldRight(agg第一次执行的初始值)(func: (集合元素类型，集合元素类型)=>集合元素类型)
    list17.foldRight(200)( (curr,agg)=>{
      println(s"agg=${agg}  curr=${curr}")
      agg - curr
    })

  }
}
