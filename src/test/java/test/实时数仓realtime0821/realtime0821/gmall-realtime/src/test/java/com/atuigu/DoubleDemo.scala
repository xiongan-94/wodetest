package com.atuigu

import java.text.DecimalFormat

/**
 * Author atguigu
 * Date 2021/1/15 14:42
 */
object DoubleDemo {
    def main(args: Array[String]): Unit = {
        /*System.out.println(1 - 0.9 == 0.1);
        System.out.println(1 - 0.1 == 0.9);
        System.out.println(1-0.9);*/

        //println(BigDecimal("1") - BigDecimal(0.9) == BigDecimal(0.1))

        val formatter = new DecimalFormat(".00%")
        println(formatter.format(12.369))
        println(formatter.format(2.364))
        println(formatter.format(2345.364))
        println(formatter.format(0.12347))

    }
}
