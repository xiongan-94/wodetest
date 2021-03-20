package com.atgugiu.realtime.ads


import java.time.LocalDateTime

import com.atgugiu.realtime.BaseAppV1
import com.atgugiu.realtime.bean.OrderWide
import com.atgugiu.realtime.util.Constant
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka010.OffsetRange
import org.json4s.jackson.JsonMethods
import scalikejdbc.config.DBs
import scalikejdbc.{DB, SQL}

import scala.collection.mutable.ListBuffer

object AdsOrderWide extends BaseAppV1 {
    override val master: String = "local[2]"
    override val appName: String = "AdsOrderWide"
    override val batchTime: Long = 5
    override val groupId: String = "AdsOrderWide"
    override val topics: Set[String] = Set(Constant.DWS_TOPIC)
    override val offsetWhere: String = "mysql"
    implicit val f = org.json4s.DefaultFormats

    override def run(ssc: StreamingContext,
                     sourceStream: DStream[ConsumerRecord[String, String]],
                     offsets: ListBuffer[OffsetRange]): Unit = {
        // 每5s计算一次每个品牌的分摊金额 : 按照品牌分组, 对分摊金额进行聚合
        val tmAndAmount = sourceStream
            .map(record => {
                val orderWide = JsonMethods.parse(record.value()).extract[OrderWide]
                (orderWide.tm_id, orderWide.tm_name) -> orderWide.final_detail_amount

            })
            .reduceByKey(_ + _)

        // 自动加载配置文件
        DBs.setup()
        tmAndAmount.foreachRDD(rdd => {
            // 在driver中向外写
            // 数据
            val time = LocalDateTime.now()
            val data = rdd.collect()
                .map {
                    case ((tm_id, tm_name), amount) =>
                        Seq(time, tm_id, tm_name, amount)
                }
            // offset
            val offset = offsets
                .map((range: OffsetRange) => {
                    Seq(groupId, range.topic, range.partition, range.untilOffset)
                })
            // 放入一个事务中向外写
            DB.localTx(implicit session => {
                // 这里的代码都会在一个事务中执行, 要么都成功, 要么都失败
                val dataSql =
                    """
                      |replace into tm_amount values(?, ?, ?, ?)
                      |""".stripMargin
                data.foreach(println)
                SQL(dataSql).batch(data: _*).apply()

                val OffsetSql =
                    """
                      |replace into ads_offset values(?, ?, ?, ?)
                      |""".stripMargin
                SQL(OffsetSql).batch(offset: _*).apply()

            })
        })

    }
}

/*
手动偏移量+事务

向mysql写数据需要开启事务!

driver
    只需要开启一个事务
executor
    有多少个分区,就需要开启多少事务

    这多个事务由于属于同一个批次,那么他们应该同时成功或同时失败

    分布式事务


 val conn = DriverManager.getConnection("")

try {
    conn.setAutoCommit(false)
    // 数据拉倒驱动, 向外写
    // 偏移量数据, 向外写
} catch {
    case e: SQLException =>
        conn.rollback()
} finally {
    conn.commit()
    conn.setAutoCommit(true)
}




 */