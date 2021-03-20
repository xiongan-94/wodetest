package com.atguigu.chapter02

object $06_String {

  /**
    * java中如何得到一个字符串:
    *     1、通过""包裹
    *     2、new String(..)
    *     3、通过一些方法
    *     4、字符串拼接【+】
    * scala中得到一个字符串:
    *     1、通过""包裹
    *     2、字符串拼接[插值表达式] s"${变量名/表达式}"
    *     3、new String(..)
    *     4、通过""" """包裹[能够保留字符串的输入的格式]
    *     5、通过一些方法
    * @param args
    */
  def main(args: Array[String]): Unit = {
    //1、通过""包裹
    val name = "ZHANGSAN"

    val address = "shenzhen"

    val str = name + address

    //2、字符串拼接
    val str2= s"${name}-${address}"
    println(str2)

    val str3 = s"1+1=${1+1}"
    println(str3)

    val str5 = new String("hello")
    println(str5)

    //3、通过三引号包裹
    val sql =
      """
        |create table student(
        |   id string,
        |   name string,
        |   age string)COLUMN_ENCODED_BYTES=0
      """.stripMargin
    println(sql)

    //三引号与插值表达式结合使用
    val tableName = "student"
    val sql2 =
      s"""
        |create table ${tableName}(
        |   id string,
        |   name string,
        |   age string)COLUMN_ENCODED_BYTES=0
      """.stripMargin
    println(sql2)

    println(str2.substring(2))

    val str6 = "hello %s".format("lisi")
    println("http://www.xx.com/aa/bb?ip=%s".format("192.168.1.102"))
    println("http://www.xx.com/aa/bb?ip=%d".format(10))
    println(str6)
  }
}
