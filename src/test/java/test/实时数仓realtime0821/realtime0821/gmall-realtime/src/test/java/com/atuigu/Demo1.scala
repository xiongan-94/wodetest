package com.atuigu

object Demo1 {
    def main(args: Array[String]): Unit = {
        //        foo(1, 2, 3)
        val list1 = List(30, 50, 70, 60, 10, 20)
        foo(list1: _*)
    }

    def foo(n: Int*) = {
        println(n.mkString(","))
    }
}
