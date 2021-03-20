package com.atguigu.spark.project.app

import com.atguigu.spark.project.bean.AdsLog
import com.atguigu.spark.project.util.MyJdbcUtil
import org.apache.spark.streaming.dstream.DStream

/**
 * Author atguigu
 * Date 2020/12/25 9:27
 */
object DateAreaCityAdsHandler {
    val url = "jdbc:mysql://hadoop162:3306/spark0821?user=root&password=aaaaaa&characterEncoding=utf8"
    val inserSql = "insert into user_ad_count values(?,?,?,?) on duplicate key update COUNT=count+?"
    
    def writeCountToMysql(filteredAdsLogStream: DStream[AdsLog]) = {
        filteredAdsLogStream
            .map(log => {
                ((log.logDate, log.area, log.city, log.adsId), 1L)
            })
            .reduceByKey(_ + _)
            .foreachRDD(rdd => {
                rdd.foreachPartition((it: Iterator[((String, String, String, String), Long)]) => {
                    val data = it.map {
                        case ((date, area, city, ads), count) =>
                            Array(date, area, city, ads, count, count)
                    }
                    MyJdbcUtil.writeToJdbc(url,
                        "insert into area_city_ad_count values(?,?,?,?,?) on duplicate key update count=count+?",
                        data)
                    
                })
            })
    }
    
}

