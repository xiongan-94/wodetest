package com.atguigu.chapter08

object $06_MatchList {

  def main(args: Array[String]): Unit = {

    val list = List[Int](1,2,7,9,10)
    //val list = List[Any]("spark",1,2.0)

    //第一种匹配方式
    list match {
        //匹配List只有一个元素
      case List(x) => println("List只有一个元素")
        //匹配List有三个元素,以及匹配元素的类型
      //case List(x:String,y:Int,z:Double) => println("list中有三个元素，并且元素类型为string,int,double")
        //匹配List有三个元素
      case List(x,_,z) => println("List有三个元素")
        //匹配List至少有一个元素
      case List(x,_*) => println("List至少有一个元素")
    }
    println("*"*100)

    list match {
      //匹配List只有一个元素
      case x :: Nil => println("List只有一个元素")
      //匹配List有三个元素,以及匹配元素的类型
      //case (x:String) :: (y:Int) :: (z:Double) :: Nil =>  println("list中有三个元素，并且元素类型为string,int,double")
      //匹配List有三个元素
      case x :: _ :: z :: Nil => println("List有三个元素")
      //匹配List至少有一个元素
      case x :: y ::  aaa => println(s"List至少有一个元素,${x}   ${aaa}")
    }
    println("*"*100)
    //泛型的特点: 泛型擦除，泛型只是编译器使用，用来规定集合里面的数据类型是啥，真正在编译的时候jvm虚拟机会将泛型擦掉
    list match {
      case x:List[String] => println("string list")
      case x:List[Any] => println("any list")
      case x:List[Int] => println("int list")
      case _ => println("其他list")
    }
  }

}
