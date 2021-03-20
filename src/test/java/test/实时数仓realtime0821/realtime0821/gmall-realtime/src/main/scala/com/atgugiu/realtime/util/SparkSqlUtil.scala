package com.atgugiu.realtime.util

import org.apache.spark.sql.{Encoder, SparkSession}

/**
 * Author atguigu
 * Date 2021/1/12 14:18
 */
object SparkSqlUtil {
    def getRDD[T: Encoder](spark: SparkSession,
                  url: String,
                  query: String) = {
       spark.read
            .option("url", url)
            .option("query", query)
            .format("jdbc")
            .load()
            .as[T]
            .rdd

    }
}
