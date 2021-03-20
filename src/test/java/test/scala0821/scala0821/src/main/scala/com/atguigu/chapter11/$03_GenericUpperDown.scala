package com.atguigu.chapter11

object $03_GenericUpperDown {

  /**
    * 上下限:
    *     上限【代表传入的泛型必须是指定类型或者是其子类】:  T <: 指定类型
    *     下限 【代表传入的类型必须是指定类型或者是其父类】 : T >: 指定类型
    * @param args
    */
  def main(args: Array[String]): Unit = {

    //m1(new BigBigDog)
    val big:Any = new BigBigDog

    m2(1)
  }

  class Animal

  class Dog extends Animal

  class BigDog extends Dog

  class BigBigDog extends BigDog


  def m1[T<:Dog](x:T) = {
    println(x)
  }

  def m2[U>:BigDog](x:U) = {
    println(x)
  }
}
