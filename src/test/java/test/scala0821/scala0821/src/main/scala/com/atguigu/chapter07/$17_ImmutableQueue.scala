package com.atguigu.chapter07

import scala.collection.immutable.Queue

object $17_ImmutableQueue {

  /**
    * 队列的特点: 先进先出
    * 创建方式: Queue[集合元素类型](初始元素,...)
    * @param args
    */
  def main(args: Array[String]): Unit = {

    val queue = Queue[Int](10,2,4,1,6)
    println(queue)

    //添加元素
    val queue2 = queue.+:(100)

    val queue3 = queue.:+(200)

    println(queue2)
    println(queue3)

    val queue4 = queue.++(List(9,8,7))

    val queue5 = queue.++:(List(5,6,7))

    println(queue4)
    println(queue5)

    val queue6 = queue.enqueue(1000)
    println(queue6)

    //删除元素
    val dequeue: (Int, Queue[Int]) = queue.dequeue
    println(dequeue)

    val option = queue.dequeueOption
    println(option)

    //修改
    val queue7 = queue.updated(1,20)
    println(queue7)
  }
}
