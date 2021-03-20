package com.atguigu.chapter08

object $07_MatchTuple {

  def main(args: Array[String]): Unit = {

    val t1 = ("zhangsan",20,"shenzhen")
    //元组在匹配的时候,元组的元素的个数必须与匹配条件的元组的元素个数一致
    val name = "zhangsan"
    t1 match{
      case (x:String,y:Int,z:String) => println(".........")

      case (x,y,z) => println(s"${x} ${y} ")
     // case (x,y) => println(s"${x} ${y} ${z}")
    }

    val schools = List[(String,(String,(String,Int)))](
      ("宝安中学",("大数据1班",("zhangsan",20))),
      ("宝安中学",("大数据1班",("lisi",20))),
      ("宝安中学",("大数据1班",("wangwu",20))),
      ("宝安中学",("大数据1班",("zhaoliu",20)))
    )

    //schools.map(x=> x._2._2._1 ).foreach(println)
    schools.map(x=> {
      x match {
        case (schoolName,(className,(stuName,age))) => stuName
      }
    } ).foreach(println)
  }
}
