package com.atguigu.chapter10
object AA{


  implicit val y:Int = 10
  implicit val a:Int = 100
}
object $02_ImplicitParam {

  /**
    * 隐式转换参数的语法:
    *     1、通过implicit val 变量名:类型 = 值
    *     2、def 方法名(..)(implicit 变量名:类型)
    * 如果有多个隐式转换参数/方法都符合要求，需要明确通过import 包名.隐式参数名/方法名的方式指定使用哪个隐式转换参数/方法
    * @param args
    */
  def main(args: Array[String]): Unit = {

    import AA.a

    println(m1(20))
  }


  def m1(x:Int)(implicit y:Int) = x+y

}
