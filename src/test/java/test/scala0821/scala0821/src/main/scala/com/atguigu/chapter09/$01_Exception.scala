package com.atguigu.chapter09

import java.sql.{Connection, DriverManager, PreparedStatement}

import scala.util.Try

object $01_Exception {

  /**
    * java中关于的异常的处理:
    *     1、捕获异常: try{} catch{} finally{}
    *         try{
    *           ...
    *         }catch(Exception e){
    *           ..
    *         }finally{ --用于释放资源
    *           ..
    *         }
    *     2、抛出异常: throw new ..Exception [必须在方法上throws Exception]
    * scala中关于异常处理:
    *     1、捕获异常:
    *         1、try{} catch{} finally{}   【一般用于获取外部资源的时候使用】
    *         2、Try(代码).getOrElse(代码执行失败返回的默认值)
    *             Success: 代表Try中包裹的代码执行成功,后续可以通过get方法取出代码的执行结果
    *             Filture: 代表Try中包裹的代码执行失败
    *     2、抛出异常: throw new ..Exception  [scala抛出异常不需要在方法后面通过throws Exception声明]
    * @param args
    */
  def main(args: Array[String]): Unit = {

    //println(m1(10, 0))
    println(m2(10,0))

    val list = List[String](
      "1 zhangsan 20 shenzhen",
      "2 lisi  beijing",
      "3 lisi  tianjin",
      "4 zhaoliu 55 shenzhen"
    )


    //需求: 求年龄总和
    list.map(line=>{

      val age = Try(line.split(" ")(2).toInt).getOrElse(-1)
      age

    }).filter(_!= -1).foreach(println(_))


  }

  def m1(x:Int,y:Int) = {
    if(y==0) throw new Exception("y=0")
    x/y
  }

  def m2(x:Int,y:Int) = {

    try{
      x/y
    }catch {
      case e:Exception => println("y=0")
    }
  }

  def jdbc() = {
    var connection:Connection = null
    var  statement:PreparedStatement = null
    try{

      //1、获取连接
      connection  = DriverManager.getConnection("jdbc:mysql://hadoop102:3306/test")
      //2、创建statement对象
      statement = connection.prepareStatement("insert into person values(?,?,?)")
      //3、封装参数
      statement.setString(1,"zhangsan")
      statement.setString(3,"shenzhen")
      statement.setInt(2,20)
      //4、执行
      statement.executeUpdate()
    }catch {
      case e:Exception=>
    }finally {
      //5、关闭
      if(statement!=null)
        statement.close()
      if(connection!=null)
        connection.close()
    }

  }
}
