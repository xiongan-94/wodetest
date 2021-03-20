package com.atguigu.chapter02

import scala.io.{Source, StdIn}

object $04_StdIn {

  /**
    * scala中读取控制台输入的数据: StdIn
    *
    * 读取文件内容: Source.fromFile(path,"utf-8")
    */
  def main(args: Array[String]): Unit = {

    val str = StdIn.readLine("请输入一句话")
    println(str)

    println(Source.fromFile("d:/data.txt", "utf-8").getLines().toBuffer)
  }
}
