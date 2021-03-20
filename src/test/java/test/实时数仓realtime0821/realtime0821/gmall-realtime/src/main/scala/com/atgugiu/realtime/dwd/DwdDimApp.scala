package com.atgugiu.realtime.dwd

import com.atgugiu.realtime.BaseAppV2
import com.atgugiu.realtime.bean._
import com.atgugiu.realtime.util.MyJDBCUtil
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.phoenix.spark._
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka010.OffsetRange
import org.json4s.jackson.JsonMethods

import scala.collection.mutable.ListBuffer

/**
 * Author atguigu
 * Date 2021/1/11 10:41
 *
 * // 1. 消费6个维度表, 放入一个流, 然后再分流写入到HBase
 * // 2. 消费6个维度表, 放入6个流, 然后分别写入到HBase ----
 *
 */
object DwdDimApp extends BaseAppV2 {
    override val master: String = "local[2]"
    override val appName: String = "DwdDimApp"
    override val batchTime: Long = 5
    override val groupId: String = "DwdDimApp_1"
    override val topics: Set[String] = Set(
        "ods_user_info",
        "ods_sku_info",
        "ods_spu_info",
        "ods_base_category3",
        "ods_base_province",
        "ods_base_trademark")


    def save2Phoenix[T <: Product](topic: String,
                                   offsets: Map[String, ListBuffer[OffsetRange]],
                                   dataStream: DStream[ConsumerRecord[String, String]],
                                   table: String,
                                   cols: Seq[String])(implicit mf: scala.reflect.Manifest[T]): Unit = {
        dataStream
            .map(record => {
                implicit val f = org.json4s.DefaultFormats
                val line = record.value()
                val jValue = JsonMethods.parse(line)
                jValue.extract[T]
            })
            .foreachRDD(rdd => {
                rdd.saveToPhoenix(
                    table,
                    cols,
                    zkUrl = Option("hadoop162,hadoop163,hadoop164:2181"))
                saveOffset(offsets.filter(_._1 == topic))
            })
    }

    override def run(ssc: StreamingContext,
                     sourceStream: Map[String, DStream[ConsumerRecord[String, String]]],
                     offsets: Map[String, ListBuffer[OffsetRange]]): Unit = {

        // 保存UserInfo信息的到Hbase中
        save2Phoenix[UserInfo](
            "ods_user_info",
            offsets,
            sourceStream("ods_user_info"),
            "GMALL_USER_INFO",
            Seq("ID", "USER_LEVEL", "BIRTHDAY", "GENDER", "AGE_GROUP", "GENDER_NAME"))
        save2Phoenix[ProvinceInfo](
            "ods_base_province",
            offsets,
            sourceStream("ods_base_province"),
            "GMALL_PROVINCE_INFO", //shift+ctrl+u 大小写转换
            Seq("ID", "NAME", "AREA_CODE", "ISO_CODE"))

        // 三级品类
        save2Phoenix[BaseCategory3](
            "ods_base_category3",
            offsets,
            sourceStream("ods_base_category3"),
            "GMALL_BASE_CATEGORY3", //shift+ctrl+u 大小写转换
            Seq("ID", "NAME", "CATEGORY2_ID"))

        save2Phoenix[SpuInfo](
            "ods_spu_info",
            offsets,
            sourceStream("ods_spu_info"),
            "GMALL_SPU_INFO", //shift+ctrl+u 大小写转换
            Seq("ID", "SPU_NAME"))

        save2Phoenix[BaseTrademark](
            "ods_base_trademark",
            offsets,
            sourceStream("ods_base_trademark"),
            "GMALL_BASE_TRADEMARK", //shift+ctrl+u 大小写转换
            Seq("ID", "TM_NAME"))

        val phoenixUrl = "jdbc:phoenix:hadoop162,hadoop163,hadoop164:2181"
        // sku_info 比较特殊
        sourceStream("ods_sku_info")
            .foreachRDD(rdd => {

                if (!rdd.isEmpty()) { // 0 rdd非空才需要进行join
                    // 1. 完成和其他3个维度表的*join
                    val c3s = MyJDBCUtil
                        .readFromJdbc_1[BaseCategory3](phoenixUrl, "select * from GMALL_BASE_CATEGORY3")
                        .map(e => (e.id, e))
                        .toMap
                    val tms = MyJDBCUtil
                        .readFromJdbc_1[BaseTrademark](phoenixUrl, "select ID tm_id, TM_NAME from GMALL_BASE_TRADEMARK")
                        .map(e => (e.tm_id, e))
                        .toMap
                    val spus = MyJDBCUtil
                        .readFromJdbc_1[SpuInfo](phoenixUrl, "select * from GMALL_SPU_INFO")
                        .map(e => (e.id, e))
                        .toMap

                    val resultRdd = rdd
                        .map(record => {
                            implicit val f = org.json4s.DefaultFormats
                            val json = record.value()
                            val skuInfo = JsonMethods.parse(json).extract[SkuInfo]
                            skuInfo.category3_name = c3s(skuInfo.category3_id).name
                            skuInfo.tm_name = tms(skuInfo.tm_id).tm_name
                            skuInfo.spu_name = spus(skuInfo.spu_id).spu_name
                            skuInfo
                        })

                    // 2. 写入到hbase中
                    import org.apache.phoenix.spark._
                    resultRdd.saveToPhoenix(
                        "GMALL_SKU_INFO",
                        Seq("ID", "SPU_ID", "PRICE", "SKU_NAME", "TM_ID", "CATEGORY3_ID", "CREATE_TIME", "CATEGORY3_NAME", "SPU_NAME", "TM_NAME"),
                        zkUrl = Option("hadoop162,hadoop163,hadoop164:2181"))
                    saveOffset(offsets.filter(_._1 == "ods_sku_info"))
                }
            })
    }
}

/*
写入数据到Hbase中
 通过Phoenix
 1. SparkSql 写 jdbc  (X)
        upsert

 2. spark和Phoenix的整合

 */