package com.atguigu.chapter08

object $05_MatchArray {

  def main(args: Array[String]): Unit = {

    //val arr:Array[Any] = Array(4,1,10,2,7)
    val arr:Array[Any] = Array("spark",1,2.0)

    arr match {
        //匹配数组只有一个元素
      case Array(x) => println(s"数组只有一个元素${x}")
        //匹配数组只有三个元素,匹配元素的类型
      case Array(x:String,y:Int,z:Double) => println("匹配数组有三个元素，并且元素类型分别为String,Int,Double")
        //匹配数组有三个元素
      case Array(x,y,z) => println(s"数组有三个元素${x},${y},${z}")
        //匹配数组至少有一个元素
      case Array(_,_*) => println("数组至少有一个元素")
    }
    // int[] arr = new int[] {}
    // object[] arr = new object[] {}
    // String[] arr = new String[] {}
    arr match {
      //case x:Array[Int] => println("arr是Int数组")
      case x:Array[Any] => println("arr是Any数组")
      //case x:Array[String] => println("arr是String数组")
    }
  }
}
