package com.atgugiu.realtime.dwd

import com.atgugiu.realtime.BaseAppV1
import com.atgugiu.realtime.bean.{OrderInfo, ProvinceInfo, UserInfo, UserStatus}
import com.atgugiu.realtime.util.{MyJDBCUtil, MyKafkaUtil, SparkSqlUtil}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
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
object DwdOrderInfo extends BaseAppV1 {
    override val master: String = "local[2]"
    override val appName: String = "DwdOrderInfo"
    override val batchTime: Long = 5
    override val groupId: String = "DwdOrderInfo"
    override val topics: Set[String] = Set("ods_order_info")
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

    def joinUserInfo(ssc: StreamingContext,
                     stream: DStream[OrderInfo]) = {
        val spark = SparkSession.builder()
            .config(ssc.sparkContext.getConf)
            .getOrCreate()
        import spark.implicits._

        stream
            .transform((rdd: RDD[OrderInfo]) => {
                rdd.cache()
                // 目标两个RDD进行join
                // 从Phoenix中读取到用户表的数据, 通过SparkSql
                val ids = rdd.map(_.user_id).collect.mkString("','")
                val sql = s"select * from GMALL_USER_INFO where id in('${ids}')"
                val userInfoRDD = SparkSqlUtil
                    .getRDD[UserInfo](spark, phoenixUrl, sql)
                    .map(userInfo => (userInfo.id.toLong, userInfo))

                rdd
                    .map(orderInfo => (orderInfo.user_id, orderInfo))
                    .join(userInfoRDD)
                    .map {
                        case (_, (orderInfo, userInfo)) =>
                            orderInfo.user_age_group = userInfo.age_group
                            orderInfo.user_gender = userInfo.gender_name
                            orderInfo
                    }
            })
    }

    def joinProvinceInfo(ssc: StreamingContext, stream: DStream[OrderInfo]) = {
        // 读出来所有的省份信息  driver
        val provinceList = MyJDBCUtil.readFromJdbc_1[ProvinceInfo](phoenixUrl, "select * from GMALL_PROVINCE_INFO")
        val provinceListBD = ssc.sparkContext.broadcast(provinceList)
        // 省份的数据流比较小, 所以采用map端join  没有shuffle
        stream.map(orderInfo => {
            // 拿到所有的省份信息  executor
            val provinces = provinceListBD
                .value
                .map(info => (info.id, info))
                .toMap
            val province = provinces(orderInfo.province_id.toString)
            orderInfo.province_area_code = province.area_code
            orderInfo.province_iso_code = province.iso_code
            orderInfo.province_name = province.name
            orderInfo
        })
    }

    override def run(ssc: StreamingContext,
                     sourceStream: DStream[ConsumerRecord[String, String]],
                     offsets: ListBuffer[OffsetRange]): Unit = {
        // 1. 把数据解析成样例类
        var orderInfoStreamWithoutDim = sourceStream
            .map(record => {
                implicit val f = org.json4s.DefaultFormats + toLong + toDouble
                val line = record.value()
                JsonMethods.parse(line).extract[OrderInfo]
            })




        // 2. 判断首单业务
        orderInfoStreamWithoutDim = orderInfoStreamWithoutDim
            .mapPartitions((it: Iterator[OrderInfo]) => {
                val orderInfoList = it.toList
                // user_1 user_2
                // 连接Phoenix
                // 获取这个分区内所有的下单用户  1 2 3
                val currentIds = orderInfoList.map(_.user_id).mkString("','")
                val querySql = s"select * from USER_STATUS where USER_ID in('${currentIds}')"
                // 获取这次下单用户中在Phoenix中已经存在的用户 1 2  userIdList <=currentIds
                val userIdList = MyJDBCUtil
                    .readFromJdbc_1[UserStatus](phoenixUrl, querySql)
                    .map(_.user_id)
                // 如果在Phoenix中, 就标记为false, 否则标记true
                orderInfoList
                    .map(orderInfo => {
                        if (userIdList.contains(orderInfo.user_id.toString)) {
                            orderInfo.is_first_order = false
                        } else {
                            orderInfo.is_first_order = true
                        }
                        orderInfo
                    })
                    .toIterator
            }) // 一个用户出现了多个首单
            .transform(rdd => {
                rdd
                    .groupBy(_.user_id)
                    .flatMap {
                        case (user_id, it: Iterable[OrderInfo]) =>
                            val list = it.toList
                            if (list.size > 1 && list.head.is_first_order) {
                                val sortedList = list.sortBy(_.create_time)

                                sortedList.head :: sortedList.tail.map(info => {
                                    info.is_first_order = false
                                    info
                                })
                            } else {
                                list
                            }
                    }
            })
        // 3. 与维度表进行join, 补齐相关的维度信息. 维度表数据来源于: HBase(Phoenix)
        val streamWithUserInfo = joinUserInfo(ssc, orderInfoStreamWithoutDim)
        val resultStream = joinProvinceInfo(ssc, streamWithUserInfo)

        // 4. 数据写出
        resultStream.foreachRDD((rdd: RDD[OrderInfo]) => {
            rdd.cache()
            // 1. 写出用户状态
            import org.apache.phoenix.spark._
            rdd
                .map(orderInfo => UserStatus(orderInfo.user_id.toString))
                .saveToPhoenix("USER_STATUS",
                    Seq("USER_ID"),
                    zkUrl = Option("hadoop162,hadoop163,hadoop164:2181"))
            // 2. 订单数据写入到DWD层(es)
            /*rdd.foreachPartition((it: Iterator[OrderInfo]) => {
                MyESUtil.insertBulk(s"gmall_order_info_${LocalDate.now()}", it.map(orderInfo => (orderInfo.id.toString, orderInfo)))
            })*/
            // 2. 订单数据写入到DWD层(Kafka), 给dws层的双流join做准备
            rdd.foreachPartition((it: Iterator[OrderInfo]) => {
                MyKafkaUtil.sendToKafka(it.map(info => {
                    implicit val f = org.json4s.DefaultFormats
                    val data = Serialization.write(info)
                    ("dwd_order_info", data)
                }))

            })

            // 3. 保存偏移量
            saveOffset(offsets)
        })
    }
}

/*
如何判断首单:
    存储状态?
        存储用户id, 如果已经存储过, 就不是, 如果是第一次存就是

     存储的时间的持久性:  mysql hbase  hive es
     存取速度方面:  redis mysql hbase es
     数据量: hbase es
     读写变量: hbase

-----

join:
    使用spark提供的rdd的join

    1. orderInfoRDD.join(userInfoRDD)
        适合两个RDD都比较大, 分布式join

    2. 一个大一个小
        使用map端join, 使用一个大表一个小表

        对大表做map, 然后把小表广播出去


----------
order_info  user_info  province_info

order_info 大 user_info 大

resultRDD 大 province_info 小


[U : Encoder]
    必须有一个隐式值 他的类型是  Encoder[U]

 */