package com.atguigu.chapter02

object $07_DateConvertion {

  /**
    * 数字和数字的转换
    *    1、低精度向高精度转换: Int->Long
    *         java中: 自动转换
    *         scala中: 自动转换
    *    2、高精度转低精度: Long->Int
    *         java中: 强转
    *         scala: toXXX方法
    * 数字和字符串的转换
    *   1、数字转字符串
    *       java: 字符串拼接
    *       scala: 字符串拼接\toString
    *   2、字符串转数字
    *       java: Integer.ValueOf(字符串),..
    *       scala: toXXX方法
    */
  def main(args: Array[String]): Unit = {

    //低精度向高精度转换
    val a:Int = 10

    val b:Long = a

    //高精度转低精度
    val c:Long  = 100
    val d:Int = c.toInt
    println(d)

    //数字转字符串
    val s:String = c.toString
    val s1:String = s"${c}"
    println(s)
    println(s1)

    //字符串转数字
    val s2 = "10"
    val e:Int = s2.toInt
    println(e)

    //字符串中.不一定是小数点
    val s3 = "10.0"
    //val f:Int = s3.toInt
    //println(f)

    val g:Double = s3.toDouble
    println(g)
  }
}
