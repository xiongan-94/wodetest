package com.atuigu

import com.atgugiu.realtime.bean.StartupLog
import org.json4s.JsonAST.JObject
import org.json4s.jackson.{JsonMethods, Serialization}

/**
 * Author atguigu
 * Date 2021/1/5 9:54
 */
object Json4sDemo {



    def main(args: Array[String]): Unit = {
        // json4s:  json for scala  spark已经内置, 所以不需要手动导入\
//        parse()

        write()
    }

    def parse(): Unit = {
        // 有json字符串, 编程代码中对象的过程
        // 1. parse
        // 2. extract
        val s =
            """
              |{"common":{"ar":"310000","ba":"Xiaomi","ch":"web","md":"Xiaomi Mix2 ","mid":"mid_44","os":"Android 10.0","uid":"156","vc":"v2.1.134"},"start":{"entry":"icon","loading_time":14829,"open_ad_id":15,"open_ad_ms":8174,"open_ad_skip_ms":0},"ts":1609723695000}
              |""".stripMargin

        val jv = JsonMethods.parse(s)
        val jCommon = jv \ "common"
        val JTs = jv \ "ts"
        implicit val f = org.json4s.DefaultFormats
        val log = (jCommon.merge(JObject("ts" -> JTs))).extract[StartupLog]
        println(log)

        println((jv \ "common" \ "vc").extract[String])

    }

    def write(): Unit = {
        // 把代码中的对象, 转成json字符串的过程
        //val map = Map("a" -> 97, "b" -> 98)
        val map = Array("a", "b", "c")
        implicit val f = org.json4s.DefaultFormats
        val s = Serialization.write(map)
        println(s)
    }
}
