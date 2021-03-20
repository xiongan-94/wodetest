package com.atgugiu.realtime.dws

import java.text.DecimalFormat

import com.atgugiu.realtime.BaseAppV2
import com.atgugiu.realtime.bean.{OrderDetail, OrderInfo, OrderWide}
import com.atgugiu.realtime.util.{ConfigUtil, Constant, MyKafkaUtil, MyRedisUtil}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka010.OffsetRange
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.json4s.jackson.{JsonMethods, Serialization}
import redis.clients.jedis.Jedis

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer


/**
 * Author atguigu
 * Date 2021/1/13 14:12
 * 消费2个topic: dwd_order_detail dwd_order_info , 2个流
 *
 */
object DwsOrderWide extends BaseAppV2 {
    override val master: String = "local[4]"
    override val appName: String = "DwsOrderWide"
    override val batchTime: Long = 5
    override val groupId: String = "DwsOrderWide"
    override val topics: Set[String] = Set("dwd_order_info", "dwd_order_detail")
    implicit val f = org.json4s.DefaultFormats

    val preOrderInfo = "order_info:"
    val preOrderDetail = "order_detail:"

    // 第一种join: 窗口join
    def join_1(orderInfoStream: DStream[(Long, OrderInfo)],
               orderDetailStream: DStream[(Long, OrderDetail)]) = {
        val orderInfoWithWindow = orderInfoStream.window(Seconds(batchTime * 4), Seconds(batchTime))
        val orderDetailWithWindow = orderDetailStream.window(Seconds(batchTime * 4), Seconds(batchTime))

        // join 后去重:   借助redis  Set集合 存储?   存储order_detail的id就可以
        orderInfoWithWindow
            .join(orderDetailWithWindow)
            .map {
                case (orderId, (orderInfo, orderDetail)) =>
                    new OrderWide(orderInfo, orderDetail)
            }
            .mapPartitions(it => {
                val client = MyRedisUtil.getClient()
                val r = it.filter(wide => {
                    val ok = 1 == client.sadd("order_detail_id_set", wide.order_detail_id.toString)
                    client.expire("order_detail_id_set", 30 * 60)
                    ok
                })
                client.close()
                r
            })
    }

    // 缓存orderDetail
    def cacheOrderDetail(client: Jedis, orderDetail: OrderDetail) = {
        client.sadd(preOrderDetail + orderDetail.order_id, Serialization.write(orderDetail))
    }

    // 缓存orderInfo
    def cacheOrderInfo(client: Jedis, orderInfo: OrderInfo) = {
        client.setex(preOrderInfo + orderInfo.id, 30 * 60, Serialization.write(orderInfo))
    }

    def join_2(orderInfoStream: DStream[(Long, OrderInfo)],
               orderDetailStream: DStream[(Long, OrderDetail)]) = {
        orderInfoStream
            .fullOuterJoin(orderDetailStream)
            .mapPartitions(it => {
                val client = MyRedisUtil.getClient()

                val result = it.flatMap {
                    case (orderId, (Some(orderInfo), Some(orderDetail))) =>
                        println("some some")
                        //0. orderInfo必须存储到缓存中
                        cacheOrderInfo(client, orderInfo)
                        // 1. orderInfo  orderDetail 直接join
                        val orderWide = new OrderWide(orderInfo, orderDetail)
                        // 2. 去orderDetail的缓存找数据
                        val key = preOrderDetail + orderId
                        if (client.exists(key)) {
                            // 2. 找到就join, 把orderDetail删除
                            val orderDetailJsons = client.smembers(key)
                            client.del(key)
                            orderWide :: orderDetailJsons.asScala
                                .map(json => {
                                    val orderDetail = JsonMethods.parse(json).extract[OrderDetail]
                                    new OrderWide(orderInfo, orderDetail)
                                })
                                .toList
                        } else {
                            orderWide :: Nil
                        }
                    case (orderId, (Some(orderInfo), None)) =>
                        println("some none")
                        //0. orderInfo必须存储到缓存中
                        cacheOrderInfo(client, orderInfo)
                        // 1. 去orderDetail的缓存找数据
                        val key = preOrderDetail + orderId
                        if (client.exists(key)) {
                            // 2. 找到就join, 把orderDetail删除
                            val orderDetailJsons = client.smembers(key)
                            client.del(key)
                            orderDetailJsons
                                .asScala
                                .map(json => {
                                    val orderDetail = JsonMethods.parse(json).extract[OrderDetail]
                                    new OrderWide(orderInfo, orderDetail)
                                })
                                .toList

                        } else {
                            Nil
                        }
                    case (orderId, (None, Some(orderDetail))) =>
                        println("none some")
                        // 1. 去对方的缓存找数据
                        val key = preOrderInfo + orderId
                        if (client.exists(key)) {
                            // 2. 找到, join 返回结果 不能删除orderInfo的信息
                            val orderInfoJson = client.get(key)
                            val orderInfo = JsonMethods.parse(orderInfoJson).extract[OrderInfo]
                            new OrderWide(orderInfo, orderDetail) :: Nil
                        } else {
                            // 3. 没有找到, 把自己缓存
                            cacheOrderDetail(client, orderDetail)
                            Nil
                        }

                }
                client.close()
                result
            })
            .print()


    }

    def join_3(orderInfoStream: DStream[(Long, OrderInfo)],
               orderDetailStream: DStream[(Long, OrderDetail)]) = {
        orderInfoStream
            .fullOuterJoin(orderDetailStream)
            .mapPartitions(it => {
                val client = MyRedisUtil.getClient()

                val result = it.flatMap {
                    case (orderId, (Some(orderInfo), opt)) =>
                        println("some opt")
                        //0. orderInfo必须存储到缓存中
                        cacheOrderInfo(client, orderInfo)
                        // 1. orderInfo  orderDetail 直接join
                        val orderWideList = opt
                            .map(orderDetail => new OrderWide(orderInfo, orderDetail))
                            .toList
                        // 2. 去orderDetail的缓存找数据
                        val key = preOrderDetail + orderId
                        if (client.exists(key)) {
                            // 2. 找到就join, 把orderDetail删除
                            val orderDetailJsons = client.smembers(key)
                            client.del(key)
                            orderWideList ::: orderDetailJsons.asScala
                                .map(json => {
                                    val orderDetail = JsonMethods.parse(json).extract[OrderDetail]
                                    new OrderWide(orderInfo, orderDetail)
                                })
                                .toList
                        } else {
                            orderWideList ::: Nil
                        }

                    case (orderId, (None, Some(orderDetail))) =>
                        println("none some")
                        // 1. 去对方的缓存找数据
                        val key = preOrderInfo + orderId
                        if (client.exists(key)) {
                            // 2. 找到, join 返回结果 不能删除orderInfo的信息
                            val orderInfoJson = client.get(key)
                            val orderInfo = JsonMethods.parse(orderInfoJson).extract[OrderInfo]
                            new OrderWide(orderInfo, orderDetail) :: Nil
                        } else {
                            // 3. 没有找到, 把自己缓存
                            cacheOrderDetail(client, orderDetail)
                            Nil
                        }

                }
                client.close()
                result
            })
    }

    def moneyShare(orderWideStream: DStream[OrderWide]) = {
        orderWideStream.mapPartitions(it => {
            val formatter = new DecimalFormat(".00")
            val client = MyRedisUtil.getClient()
            val result = it.map(wide => {
                // 存储详情原始金额的和
                val originKey = "origin_" + wide.order_id
                val shareKey = "share_" + wide.order_id

                // 1. 针对该订单,获取前面所有的详情的原始金额的和
                val originSum =
                    if (client.exists(originKey)) BigDecimal(client.get(originKey))
                    else BigDecimal(0)
                // 2. 针对该订单,获取前面所有的详情的分摊金额的和
                val shareSum =
                    if (client.exists(shareKey))
                        BigDecimal(client.get(shareKey))
                    else BigDecimal(0)

                // 3. 当前详情的原始金额
                val currentOrigin = BigDecimal(wide.sku_price) * BigDecimal(wide.sku_num)
                // 4. 判断当前详情是不是最后一个
                if (BigDecimal(wide.original_total_amount) - originSum == currentOrigin) {
                    // 4.1 使用减法来计算分摊
                    wide.final_detail_amount = (wide.final_total_amount - shareSum).doubleValue()

                    client.del(originKey)
                    client.del(shareKey)

                } else {
                    // 4.2 不是最后最后一个详情: 使用除法来计算分摊
                    val currentShare = currentOrigin / BigDecimal(wide.original_total_amount) * BigDecimal(wide.final_total_amount)
                    wide.final_detail_amount = formatter.format(currentShare.doubleValue()).toDouble
                    // 1.23756  * 100 =123.756 => 124 => 124/100 = 1.24


                    // currentShare + 前面详情总分摊(shareSum) 写入到redis中
                    val totalShare = wide.final_detail_amount + shareSum
                    client.set(shareKey, totalShare.toString)

                    val totalOrigin = currentOrigin + originSum
                    client.set(originKey, totalOrigin.toString)
                }
                wide
            })
            client.close()
            result
        })
    }

    override def run(ssc: StreamingContext,
                     sourceStream: Map[String, DStream[ConsumerRecord[String, String]]],
                     offsets: Map[String, ListBuffer[OffsetRange]]): Unit = {


        val orderInfoStream = sourceStream("dwd_order_info")
            .map(record => {
                val orderInfo = JsonMethods.parse(record.value()).extract[OrderInfo]
                orderInfo.id -> orderInfo
            })

        val orderDetailStream = sourceStream("dwd_order_detail")
            .map(record => {
                val orderDetail = JsonMethods.parse(record.value()).extract[OrderDetail]
                orderDetail.order_id -> orderDetail
            })
        // 双流join第一种实现思路: 窗口join
        //        val orderWideStream = join_1(orderInfoStream, orderDetailStream)
        val orderWideStream = join_3(orderInfoStream, orderDetailStream)

        val resultStream = moneyShare(orderWideStream)
        // 保存到 clickhouse 中

        val spark = SparkSession.builder()
            .config(ssc.sparkContext.getConf)
            .getOrCreate()
        resultStream.foreachRDD(rdd => {
            println("-------------------------------------------")
            /*rdd
                .toDF
                .write
                .option("batchsize", "100")
                .option("isolationLevel", "NONE") // 设置没有事务
                .option("numPartitions", "2") // 设置并发
                //.option("driver", "ru.yandex.clickhouse.ClickHouseDriver")
                .mode(SaveMode.Append)
                .jdbc("jdbc:clickhouse://hadoop162:8123/gmall", "order_wide", new Properties())*/

            rdd.foreachPartition(wideIt => {
                MyKafkaUtil.sendToKafka(wideIt.map(wide => {
                    (Constant.DWS_TOPIC, Serialization.write(wide))
                }))
            })
            saveOffset(offsets)

        })

    }
}

/*
orderInfo的数据如何存储?
    容易存取  通过orderId能够找到这个orderInfo

key                             value
"order_info:" + orderId          orderInfo所有数据的json格式 {"id": ...}

----
orderDetail如何存储
key                             value(set)  order_detail的json数据
"order_detail:" + orderId       Set({...}, {...})

----------
如何把数据写入到clickhouse中?
1. 使用标准jdbc
2. spark-sql


 */