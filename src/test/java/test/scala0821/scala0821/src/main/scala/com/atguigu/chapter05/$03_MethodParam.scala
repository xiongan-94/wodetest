package com.atguigu.chapter05

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object $03_MethodParam {

  /**
    * java方法参数:
    *     1、public 返回值类型 方法名(类型 参数名,..) { 方法体 }
    *     2、可变参数: public 返回值类型 方法名(类型... 参数名) { 方法体 }
    *  scala方法的参数
    *      1、正常形式: def 方法名(参数名:类型,..):返回值类型 = {方法体}
    *      2、默认值: def 方法名(参数名:类型=默认值,..):返回值类型 = {方法体}
    *         如果默认值单独使用,在定义方法的时候需要将默认值定义在参数列表最后面
    *      3、带名参数:
    *           指调用方法的时候将指定值传递给哪个参数:  方法名(参数名=值)
    *      4、可变参数: def 方法名(参数名:参数类型,..,参数名:参数类型*)
    *         可变参数必须定义在参数列表的最后面
    *         可变参数不能与默认值参数、带名参数一起使用
    *         可变参数不能直接传递集合，但是可以通过 集合名称:_*  的方式将集合的所有元素传递给可变参数
    *
    * @param args
    */
  def main(args: Array[String]): Unit = {

    println(add(2, 3))

    println(add2())

    // 如果默认值单独使用,在定义方法的时候需要将默认值定义在参数列表最后面
    println(add3(10))

    //带名参数: 指调用方法的时候将指定值传递给哪个参数
    println(add4(y=10))

    println(add5(10,20,3,4,5,6,7,8))

    val paths = getPaths(7,"/user/warehouse/hive/user_info")

    readPaths(paths:_*)
  }

  def add(x:Int,y:Int) = x+y

  //默认值: def 方法名(参数名:类型=默认值,..):返回值类型 = {方法体}

  def add2(x:Int=10,y:Int=20) = x+y

  // 如果默认值单独使用,在定义方法的时候需要将默认值定义在参数列表最后面
  def add3(y:Int,x:Int=10) = x+y

  def add4(x:Int=10,y:Int) = x+y


  def add5(x:Int,y:Int,z:Int*) = {

    x+y+z.sum
  }

  /**
    * /user/warehouse/hive/user_info/20201017
    * /user/warehouse/hive/user_info/20201016
    * /user/warehouse/hive/user_info/20201015
    * /user/warehouse/hive/user_info/20201014
    * /user/warehouse/hive/user_info/20201013
    * /user/warehouse/hive/user_info/20201012
    * /user/warehouse/hive/user_info/20201011
    * /user/warehouse/hive/user_info/20201010
    *
    * 需求: 统计前7天的用户注册数
    */
  def readPaths(path:String*): Unit ={
    //模拟读取数据
    println(path.toBuffer)
  }

  def getPaths(n:Int,pathPrefix:String) = {

    //获取当前日期
    val currentDate = LocalDateTime.now()

    for(i<- 1 to n) yield {
      //日期加减法
      val time = currentDate.plusDays(-i)
      //拼接路径
      val str = time.format(DateTimeFormatter.ofPattern("yyyyMMdd"))

      s"${pathPrefix}/${str}"
    }

  }


}
