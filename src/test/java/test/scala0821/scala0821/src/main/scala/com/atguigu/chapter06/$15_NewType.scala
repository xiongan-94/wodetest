package com.atguigu.chapter06
import java.util.{HashMap=>JavaHashMap}
object $15_NewType {

  /**
    * 新类型: 给类起别名
    * type myarr = Array[String]
    */
  def main(args: Array[String]): Unit = {
    type s = String
    type myarr = Array[String]
    val name:s = "name"
    val arr:myarr = Array[String]("....")
  }
}
