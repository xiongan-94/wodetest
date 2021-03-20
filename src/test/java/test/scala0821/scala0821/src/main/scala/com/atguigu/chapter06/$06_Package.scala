package com.atguigu.chapter06

import java.util
import java.util.{HashMap => _, _}

import scala.collection.mutable

object $06_Package {
  private[chapter06] val AAAAAAA="lisi"
  /**
    * 包的作用:
    *   1、区分同名
    *   2、便于管理
    * java中对于包的申明方式:
    *     1、通过package 包名在.java源文件的第一行
    * java中对于包的使用:
    *     1、import 包名.*
    *     2、import 包名.类名
    *java中import导入包必须在声明包后面,class前面
    *
    * scala中对于包的申明方式:
    *     1、通过package 包名在.java源文件的第一行
    *     2、通过package 包名{}[通过该方式声明的包在项目结构下看不到但是在target目录下可以看到]
    * scala中对于包的使用:
    *     1、导入包下所有类: import 包名._
    *     2、导入包下某个类: import 包名.类名
    *     3、导入包下多个类: import 包名.{类名1,类名2,..}
    *     4、导入包下某个类,并起别名: import 包名.{类名=>别名}
    *     5、导入包下除开某个类的其他类: import 包名.{类名=>_,_}
    * scala可以在任何地方导入包。
    *     如果是在父作用域中导入的包，子作用域可以使用
    *     子作用域导入的包,父作用域不可以使用
    * scala中访问修饰符可以搭配package使用:  private[包名] val 属性名:类型 = 值  【代表该属性只能在该包下使用】
    *
    * 包对象:
    *     语法: package object 包名{...}
    *  包对象中非private修饰的属性/方法/函数可以在包下任何地方使用
    */
  def main(args: Array[String]): Unit = {


    import scala.util.control.Breaks._

    val scalaMap = new mutable.HashMap[String,String]()
  }

}

package bb{
  class Student
}
