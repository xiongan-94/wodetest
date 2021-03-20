package com.atguigu.chapter08

object $10_ParticalFunction {

  /**
    * 偏函数: 没有match关键字的模式匹配称之为偏函数
    * 语法:  val 函数名:PartialFunction[IN,OUT] = {
    *         case 条件1 =>...
    *         case 条件2 => ..
    *         ...
    *    }
    * IN: 代表函数的参数类型
    * OUT: 函数的返回值类型
    * @param args
    */
  def main(args: Array[String]): Unit = {

    
    val func:PartialFunction[String,Int] = {
      case "hadoop" =>
        println("hadoop......")
        10
      case "spark" =>
        println("spark..........")
        20
      case _ =>
        println("其他.......")
        -1
    }

    println(func("hadoop"))


    val schools = List[(String,(String,(String,Int)))](
      ("宝安中学",("大数据1班",("zhangsan",20))),
      ("宝安中学",("大数据1班",("lisi",20))),
      ("宝安中学",("大数据1班",("wangwu",20))),
      ("宝安中学",("大数据1班",("zhaoliu",20)))
    )

    //schools.map(_._2._2._1).foreach(println(_))
    /*schools.map(x=>{
      x match {
        case (schoolName,(className,(stuName,age))) =>stuName
      }
    }).foreach(println(_))*/

    val getStuName:PartialFunction[(String,(String,(String,Int))),String] = {
      case (schoolName,(className,(stuName,age))) => stuName
    }


    //schools.map(getStuName).foreach(println(_))
    //偏函数使用的正确姿势
    schools.map{

      case (schoolName,(className,(stuName,age))) => stuName

    }.foreach{println(_)}
  }
}
