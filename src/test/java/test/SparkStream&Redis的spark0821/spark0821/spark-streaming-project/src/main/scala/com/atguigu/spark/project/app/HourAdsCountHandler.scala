package com.atguigu.spark.project.app

import com.atguigu.spark.project.bean.AdsLog
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Minutes, Seconds}

/**
 * Author atguigu
 * Date 2020/12/25 10:14
 */
object HourAdsCountHandler {
    def statHourAscCount(adsLogStream: DStream[AdsLog]) = {
        // ((广告, 分钟),1) => ((广告, 分钟),count)
        // => (广告, (分钟, count)) => groupByKey
        adsLogStream
            .window(Minutes(60), Seconds(9))
            .map(log => (log.adsId, log.logMinute) -> 1L)
            .reduceByKey(_ + _)
            .map {
                case ((ads, minute), count) => ads -> (minute, count)
            }
            .groupByKey()
            .mapValues(_.toList.sortBy(_._1))
            .print()
    }
    
}
