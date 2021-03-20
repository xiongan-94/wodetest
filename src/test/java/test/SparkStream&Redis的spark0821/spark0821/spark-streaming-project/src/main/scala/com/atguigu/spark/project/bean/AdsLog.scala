package com.atguigu.spark.project.bean

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date

case class AdsLog(timestamp: Long,
                  area: String,
                  city: String,
                  userId: String,
                  adsId: String){
    
    val logDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date(timestamp))
    val logMinute = new SimpleDateFormat("HH:mm").format(new Date(timestamp))
}