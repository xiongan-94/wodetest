package com.atgugiu.realtime.util

import java.util.Properties

/**
 * Author atguigu
 * Date 2021/1/18 14:11
 */
object ConfigUtil {
    val is = ClassLoader.getSystemResourceAsStream("config.properties")
    val props = new Properties()
    props.load(is)
    // 传入一个配合的名字, 返回具体的值
    def getConf(name: String) ={
        props.getProperty(name)
    }

    def main(args: Array[String]): Unit = {
        println(getConf("dws_topic"))
    }
}
