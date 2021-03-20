package com.atguigu.chapter11

object $04_GenericChange {

  class Parent

  class Sub1 extends Parent

  class Sub2 extends Sub1

  //class AA[T]
  //class AA[+T]
  class AA[-T]


  /**
    * 非变: 泛型有父子关系，但是泛型对象没有任何关系 T
    * 协变: 泛型有父子关系,泛型对象继承了泛型的父子关系  +T
    * 逆变: 泛型有父子关系,泛型对象颠倒了泛型的父子关系 -T
    * @param args
    */
  def main(args: Array[String]): Unit = {

    var list1 = List[B](new B,new B)

    var list2 = List[B1](new B1,new B1)

    list1 = list2

    println(list1)

    //非变
    //var aa1 = new AA[Parent]
    //var aa2 = new AA[Sub1]

    //aa1 = aa2

    //协变
   //var aa1 = new AA[Parent]
   //var aa2 = new AA[Sub1]

   //aa1 = aa2
   //println(aa1)

    //逆变
    var aa1 = new AA[Parent]
    var aa2 = new AA[Sub1]

    aa2 = aa1
    println(aa2)

  }

  class B

  class B1 extends B
}
