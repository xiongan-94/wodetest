package com.atguigu.realtime.gmallpublisher.service

/**
 * Author atguigu
 * Date 2021/1/9 9:26
 */
object DSLUtil {
    def getTotalDauDSL(): String =
        """
          |{
          |  "query": {"match_all": {}}
          |}
          |""".stripMargin

    def getHourDauDSL() =
        """|{
           |    "aggs": {
           |      "group_by_loghour": {
           |        "terms": {
           |          "field": "logHour",
           |          "size": 24
           |        }
           |      }
           |    }
           |}""".stripMargin
}
