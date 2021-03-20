package com.atguigu.chapter08

object $08_MatchCaseClass {

  //样例类
  case class Person(name:String,var age:Int)

/*  class Student(val name:String ,var age:Int)

  object Student{

    def apply(name:String,age:Int) = new Person(name,age)
  }*/

  case class School(name:String,clazz:Clazz)

  case class Clazz(name:String,stu:Student)

  case class Student(name:String,age:Int)

  abstract class Sex

  case object Man extends Sex

  case object Woman extends Sex

  def xx(sex:Sex): Unit ={

  }

  class Animal(val name:String,val age:Int)

  object Animal{

    def unapply(arg: Animal): Option[(String, Int)] = {
      if(arg==null){
        None
      }else{
        Some((arg.name,arg.age))
      }

    }

  }

  /**
    * 样例类: case class 类名(val/var 属性名:属性类型,....)
    *     val修饰的属性不可变
    *     var修饰的属性可变
    *     val/var可以省略不写，如果省略默认就是val修饰的
    * 样例创建对象: 类名(属性值,..)
    *
    * 样例对象: case object object名称
    *样例对象一般作为枚举使用。
    * @param args
    */
  def main(args: Array[String]): Unit = {

    //对象创建
    val person = Person("zhangsan",20)
    println(person)
    println(person.name)
    println(person.age)

    //val student = Student("lisi",30)
    //println(student.name)

    val schools = List[School](
      School("宝安中学",Clazz("大数据1班",Student("zhangsan",20))),
      School("宝安中学",Clazz("大数据1班",Student("lisi",20))),
      School("宝安中学",Clazz("大数据1班",Student("wangwu",20))),
      School("宝安中学",Clazz("大数据1班",Student("zhaoliu",20)))
    )

    schools.map(x=>x.clazz.stu.name).foreach(println)

    xx(Man)


    person match {
      case Person(x,y) => println(s"${x}  ${y}")
    }

    val wangcai = new Animal("wangcai",5)

    wangcai match {
      case Animal(x,y) => println(s"${x}  ${y}")
    }
  }
}
