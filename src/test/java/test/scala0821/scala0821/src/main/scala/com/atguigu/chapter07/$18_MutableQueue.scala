package com.atguigu.chapter07

import scala.collection.mutable

object $18_MutableQueue {

  def main(args: Array[String]): Unit = {

    //创建方式
    val queue = mutable.Queue[Int](10,20,3,5,1)

    println(queue)

    //添加元素
    val queue2 = queue.+:(30)

    val queue3 = queue.:+(40)

    queue.+=(50)
    queue.+=:(60)
    println(queue2)
    println(queue3)
    println(queue)

    val queue4 = queue.++(List(1,2,3))

    val queue5 = queue.++:(List(4,5,6))

    println(queue4)
    println(queue5)

    queue.++=(List(7,8,9))
    println(queue)

    queue.enqueue(10,20,30,40)
    println(queue)

    //删除元素
    println(queue.dequeue())
    println(queue)

    //修改
    queue.update(0,20)
    println(queue)


  }
}
