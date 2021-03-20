package com.atuigu

import java.time.LocalDate

/**
 * Author atguigu
 * Date 2021/1/8 10:27
 */
object ImplicitDemo {
    def main(args: Array[String]): Unit = {
        val ago = "ago"
        val later = "later"
        2 days ago // 2.days(ago)
        2 days later // 2.days(ago)


    }

    implicit class RichInt(day: Int) {
        def days(when: String): Unit = {
            when match {
                case "ago" =>
                    println(LocalDate.now().minusDays(day))
                case "later" =>
                    println(LocalDate.now().plusDays(day))
                case _ =>
            }
        }
    }

}
