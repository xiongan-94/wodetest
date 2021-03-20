package com.atguigu.chapter06

import scala.util.Random

object $13_TypeCheck {
  class Animal{

    def xx() = println("Animal")
  }

  class Dog extends Animal{
    val name= "xx"
    override def xx() = println("Dog")
  }

  class Pig extends Animal{
    val age= 20
    override def xx() = println("Dog")

  }

  def getAnimal() = {
    val r = Random.nextInt(10)
    if(r%5==0)
      new Dog
    else if(r%2==0)
      new Pig
    else
      new Animal
  }
  /**
    * java类型判断: 对象 instanceof 类型
    * java中类型的强转: (类型)对象
    *
    * scala类型判断: 对象.isInstanceOf[类型]
    * scala中类型强转: 对象.asInstanceOf[类型]
    *
    * java中获取对象的class形式: 对象.getClass()
    * java中获取类的class形式: 类名.class
    *
    * scala中获取对象的class形式: 对象.getClass()
    * scala中获取类的class形式: classOf[类名]
    *
    *
    */
  def main(args: Array[String]): Unit = {

    val animal = getAnimal()
    println(animal.getClass)
    if(animal.isInstanceOf[Animal]){
      val pig = animal.asInstanceOf[Pig]
      println(pig.age)
    }else{
      val dog = animal.asInstanceOf[Dog]
      println(dog.name)
    }



  }
}
