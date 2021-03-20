package com.atguigu.chapter04
import scala.util.control.Breaks._
object $05_BreakContinue {

  /**
    * java中使用break与continue控制循环
    *       break: 结束整个循环
    *       continue: 结束本次循环立即开始下一次循环
    *
    *  scala中没有break与continue
    *  实现break:
    *     1、导入包:
    *     2、使用breakable以及break方法
    * @param args
    */
  def main(args: Array[String]): Unit = {

    //break
    var i=0
    /*while (i<=10){

      if(i==5){
        throw new Exception("......")
      }else{

        println(s"i=${i}")
        i=i+1
      }
    }*/

    //scala封装的break实现
    breakable({
      while (i<=10){
        if(i==5) break()
        println(s"i=${i}")
        i=i+1
      }
    })

    //continue

    var j=0
/*    while (j<=10){
      try{

        if(j==5){
            j=j+1
           throw new Exception("...")
        }
        println(s"j=${j}")
        j=j+1
      }catch {
        case e:Exception =>
      }
    }*/

    //使用scala的封装实现continue
    while (j<=10){

      breakable({
        j=j+1
        if(j==5) break()
        println(s"j=${j}")
      })


    }
  }


}
