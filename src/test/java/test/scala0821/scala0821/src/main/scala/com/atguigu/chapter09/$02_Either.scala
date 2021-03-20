package com.atguigu.chapter09

object $02_Either {

  def main(args: Array[String]): Unit = {

    val either = m1(10,0)

    either match {
      case Left(result) =>
        println(s"失败.....${result}")

      case Right(result) =>
        println(s"成功......${result}")
    }

  }

  def m1(x:Int,y:Int) = {
    try{

      Right(x/y)
    }catch {
      case e:Exception => Left((y,e))
    }

  }
}
