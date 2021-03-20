package com.atguigu.chapter08

import scala.io.StdIn
import scala.util.Random

object $03_MatchValue {

  def main(args: Array[String]): Unit = {

    val arr = Array("spark",1,2.0,false,30)

    val index = Random.nextInt(arr.length)

    val value:Any = arr(index)

    println(value)

    value match {
      case 1 => println("........1")
      case 2.0 => println("........2.0")
      case "spark" => println("........spark")
      case false => println("..........false")
      case _ => println("其他")
    }
  }
}
