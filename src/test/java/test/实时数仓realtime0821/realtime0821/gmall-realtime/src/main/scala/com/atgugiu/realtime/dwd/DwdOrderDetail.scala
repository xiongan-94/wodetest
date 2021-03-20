package com.atgugiu.realtime.dwd

import com.atgugiu.realtime.BaseAppV1
import com.atgugiu.realtime.bean.{OrderDetail, SkuInfo}
import com.atgugiu.realtime.util.{MyJDBCUtil, MyKafkaUtil}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka010.OffsetRange
import org.json4s.CustomSerializer
import org.json4s.JsonAST.{JDouble, JInt, JLong, JString}
import org.json4s.jackson.{JsonMethods, Serialization}

import scala.collection.mutable.ListBuffer

/**
 * Author atguigu
 * Date 2021/1/11 15:14
 */
object DwdOrderDetail extends BaseAppV1 {
    override val master: String = "local[2]"
    override val appName: String = "DwdOrderDetail"
    override val batchTime: Long = 5
    override val groupId: String = "DwdOrderDetail"
    override val topics: Set[String] = Set("ods_order_detail")
    val phoenixUrl = "jdbc:phoenix:hadoop162,hadoop163,hadoop164:2181"

    val toLong = new CustomSerializer[Long](formats =>
        ( {
            case JString(s) => s.toLong
            case JInt(s) => s.toLong
        }, {
            case n: Long => JLong(n)
        }))
    val toDouble = new CustomSerializer[Double](formats =>
        ( {
            case JString(s) => s.toDouble
            case JDouble(s) => s.toDouble
        }, {
            case n: Double => JDouble(n)
        }))

    override def run(ssc: StreamingContext,
                     sourceStream: DStream[ConsumerRecord[String, String]],
                     offsets: ListBuffer[OffsetRange]): Unit = {
        // 1. 补充维度表的数据
        val orderDetailStream = sourceStream.transform(rdd => {
            val orderDetailRdd = rdd
                .map(record => {
                    implicit val f = org.json4s.DefaultFormats + toLong + toDouble
                    JsonMethods.parse(record.value()).extract[OrderDetail]
                })
            val skuIds = orderDetailRdd.map(_.sku_id).collect().mkString("','")
            // 读取维度表的数据
            val skus = MyJDBCUtil
                .readFromJdbc_1[SkuInfo](phoenixUrl, s"select * from gmall_sku_info where id in('${skuIds}')")
                .map(sku => (sku.id, sku))
                .toMap
            orderDetailRdd.map(detail => {
                val skuInfo = skus(detail.sku_id.toString)
                detail.mergeSkuInfo(skuInfo)
            })

        })

        // 2. 把数据写入到Kafka的DWD层
        orderDetailStream.foreachRDD(rdd => {

            rdd.foreachPartition(it => {
                val topicAndOrderDetail = it.map(detail => {
                    implicit val f = org.json4s.DefaultFormats
                    "dwd_order_detail" -> Serialization.write(detail)
                })
                MyKafkaUtil.sendToKafka(topicAndOrderDetail)
            })
            saveOffset(offsets)
        })
    }
}
