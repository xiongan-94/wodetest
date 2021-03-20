package com.atguigu.chapter06

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}

object $12_TraitSelf {

  class Logger

  trait ReadAndWrite{
    _:Serializable =>
    /**
      * 从磁盘读取对象
      */
    def read() = {

      val ois = new ObjectInputStream(new FileInputStream("d:/obj.txt"))

      val obj = ois.readObject()

      ois.close()

      obj
    }

    /**
      * 将当前对象写入磁盘
      */
    def write() = {

      val oos = new ObjectOutputStream(new FileOutputStream("d:/obj.txt"))

      oos.writeObject(this)

      oos.close()
    }
  }

  class Person(val name:String,val age:Int) extends ReadAndWrite with Serializable

  /**
    * 定义一个trait,trait中自带read、write两个方法能够实现将当前对象读取/写入到磁盘
    *
    * 自身类型: 子类在实现trait的时候提示必须要先继承/实现某个类型
    */
  def main(args: Array[String]): Unit = {

    val person = new Person("lisi",20)

    person.write()

    val p = new Person("zhangsan",30)
    val obj = p.read()
    println(obj.asInstanceOf[Person].name)
  }
}
