package com.atguigu.chapter04

object $03_For {
  /**

    * java的for循环：
    *   1、基本for:  for(int i=0;i<=100;i++)
    *   2、增强for： for(类型 变量: 集合/数组)
    *
    * scala中重要的两个方法：
    *   to:
    *       用法: startIndex.to(endIndex)
    *       结果: 会生成一个左右闭合的集合
    *   until:
    *       用法: startIndex.until(endIndex)
    *        结果: 会生成一个左闭右开的集合
    *   scala中方法调用有两种方式：
    *     1、对象.方法名(参数值,..)
    *     2、对象 方法名 (参数值,..) [如果方法只有一个参数,此时()可以省略]
    * scala for循环语法:  for(变量 <- 数组/集合/表达式) { 循环体 }
    * for循环的守卫:  for(变量 <- 数组/集合/表达式 if(布尔表达式)) { 循环体 }
    * 步长: for(变量 <- start to end by step)  { 循环体 }
    * 嵌套for循环: for(变量 <- 数组/集合/表达式;变量 <- 数组/集合/表达式;...) { 循环体 }
    * 变量嵌入: for(变量 <- 数组/集合/表达式;  变量2 = 值 ;变量 <- 1  to 变量2;...)
    * for默认是没有返回值的，如果想要for有返回值需要用yield表达式： for(变量 <- 数组/集合/表达式) yield { 循环体 }
    *
    * @param args
    */
  def main(args: Array[String]): Unit = {

    //基本用法
    for( i <- 1 to 10){
      println(s"i=${i}")
    }

    println("+"*100)

    //步长
    for( j<- (1 to 10).by(2) ){
      println(s"j=${j}")
    }
    println("-"*100)
    for( j<- 1 to 10 by 2 ){
      println(s"j=${j}")
    }
    println("*"*100)
    for(k<- 1 to 10){
      if(k%2==0){
        println(s"k=${k}")
      }
    }
    println("*"*100)
    //守卫
    for(k<- 1 to 10 if(k%2==0)) {
      println(s"k=${k}")
    }

    /**
      * for(k<- 1 to 10){
      *   println(s"k=${k}")
      * if(k%2==0){
      * println(s"k=${k}")
      * }
      * }
      * 此循环不能通过守卫简化，因为if语句之前还有其他的语句
      */
    println("*"*100)
    for(i<- 1 to 10){
      for(j<- i to 10){
        println(s"i+j=${i+j}")
      }
    }
    //通过scala嵌套for循环简化上面循环
    println("*"*100)
    for(i<- 1 to 10; j<- i to 10){
      println(s"i+j=${i+j}")
    }

/*    for(i<- 1 to 10){
      println(s"i=${i}")
      for(j<- i to 10){
        println(s"i+j=${i+j}")
      }
    }
    不能使用scala嵌套for循环简化
    */

    for(i<- 1 to 10 ){
      if(i%2==0){
        for(j<- i to 10){
          println(s"i+j=${i+j}")
        }
      }
    }

    for(i<- 1 to 10 if (i%2==0); j<- i to 10 ){
      println(s"i+j=${i+j}")
    }

    println("*"*100)
    for(i<- 1 to 10){
      val k = i * i
      for(j<- 1 to k){
        println(s"i+j=${i+j}")
      }
    }
    println("*"*100)
    //变量嵌入简化
    for(i<- 1 to 10; k= i*i;j<- 1 to k){
      println(s"i+j=${i+j}")
    }

    //yield表达式
    val result = for(i<- 1 to 10) yield {
      i * i
    }

    println(result.toBuffer)
    println("------------------")
    1 to 10 foreach(println(_))
  }
}
