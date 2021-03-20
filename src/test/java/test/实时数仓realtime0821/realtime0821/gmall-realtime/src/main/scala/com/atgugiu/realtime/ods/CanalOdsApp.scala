package com.atgugiu.realtime.ods

import com.atgugiu.realtime.BaseAppV1
import com.atgugiu.realtime.util.MyKafkaUtil
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka010.OffsetRange
import org.json4s.jackson.{JsonMethods, Serialization}

import scala.collection.mutable.ListBuffer

/**
 * Author atguigu
 * Date 2021/1/11 9:07
 */
object CanalOdsApp extends BaseAppV1 {
    // 分流: 消费 canal_gmall_db topi数据, 然后根据不同的数据(不同表)进入不同的topic(Kafka)
    // ODS层的数据存储到Kafka
    override val master: String = "local[2]"
    override val appName: String = "CanalOdsApp"
    override val batchTime: Long = 5
    override val groupId: String = "CanalOdsApp"
    override val topics: Set[String] = Set("canal_gmall_db")

    val tableNames = List(
        "order_info",
        "order_detail",
        "user_info",
        "base_province",
        "base_category3",
        "sku_info",
        "spu_info",
        "base_trademark")


    override def run(ssc: StreamingContext,
                     sourceStream: DStream[ConsumerRecord[String, String]],
                     offsets: ListBuffer[OffsetRange]): Unit = {
        sourceStream
            .flatMap(line => {
                implicit val f = org.json4s.DefaultFormats
                val lineValue = JsonMethods.parse(line.value())
                val dataValue = lineValue \ "data"
                val children = dataValue.children

                val tablaName = (lineValue \ "table").extract[String]
                val operate = (lineValue \ "type").extract[String]

                children.map(j => (tablaName, operate.toLowerCase(), Serialization.write(j)))
            })
            .filter {
                case (tableName, operate, data) =>
                    // 1. 对普通表只要insert和update
                    // 2. order_info 只要insert
                    tableNames.contains(tableName) &&
                        ("insert" == operate || "update" == operate)
            }
            .foreachRDD(rdd => {
                rdd.foreachPartition((it: Iterator[(String, String, String)]) => {
                    val tableAndDatas = it
                        .filter {
                            case (table, operate, data) =>
                                table != "order_info" || operate == "insert"
                        }
                        .map {
                            case (table, operate, data) =>
                                ("ods_" + table, data)
                        }
                    MyKafkaUtil.sendToKafka(tableAndDatas)

                })
                // 所有数据写成功到Kafka之后, 再保存偏移量
                saveOffset(offsets)
            })

    }
}
