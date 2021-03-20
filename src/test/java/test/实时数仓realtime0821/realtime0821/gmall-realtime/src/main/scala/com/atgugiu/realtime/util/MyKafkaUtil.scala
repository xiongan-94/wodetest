package com.atgugiu.realtime.util

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent

import scala.util.Random

/**
 * Author atguigu
 * Date 2021/1/5 9:07
 */
object MyKafkaUtil {


    var kafkaParams = Map[String, Object](
        "bootstrap.servers" -> "hadoop162:9092,hadoop163:9092,hadoop164:9092",
        "key.deserializer" -> classOf[StringDeserializer],
        "value.deserializer" -> classOf[StringDeserializer],
        "auto.offset.reset" -> "earliest", // 开始消费的时候, 如果有上一次的位置就从上次的位置开始消费, 如果没有才从这个配置开始读取
        "enable.auto.commit" -> (true: java.lang.Boolean)
    )

    /**
     * 从kafka获取流数据
     * 返回值是个Map集合, key: topic  value: topic对应的数据流
     * 特殊情况: 如果多个topic的数据放入一个流中, key是 all
     *
     * @param ssc
     * @param groupId
     * @param topics
     * @param isOne 表示是否把多个topic的数据放入一个流中
     * @return
     */
    def getStreamFromKafka(ssc: StreamingContext,
                           groupId: String,
                           topics: Set[String],
                           isOne: Boolean = true): Map[String, InputDStream[ConsumerRecord[String, String]]] = {
        kafkaParams += "group.id" -> groupId

        if (isOne) {
            val stream = KafkaUtils
                .createDirectStream[String, String](
                    ssc,
                    PreferConsistent,
                    Subscribe[String, String](topics, kafkaParams)
                )
            Map("all" -> stream)
        } else {
            // TODO 后面使用时再实现
            null
        }

    }

    def getStreamFromKafka(ssc: StreamingContext,
                           groupId: String,
                           topics: Set[String],
                           offsets: collection.Map[TopicPartition, Long], // 包含6个topic的偏移量信息
                           isOne: Boolean) = {

        kafkaParams += "group.id" -> groupId
        kafkaParams += "enable.auto.commit" -> (false: java.lang.Boolean)
        if (isOne) {
            val stream = KafkaUtils
                .createDirectStream[String, String](
                    ssc,
                    PreferConsistent,
                    Subscribe[String, String](topics, kafkaParams, offsets)
                )
            Map("all" -> stream)
        } else {
            // offsets 包含6个topic的偏移量信息
            topics
                .map(topic => {
                    kafkaParams += "group.id" -> (groupId + topic)
                    val stream = KafkaUtils
                        .createDirectStream[String, String](
                            ssc,
                            PreferConsistent,
                            Subscribe[String, String](Set(topic), kafkaParams, offsets.filter(_._1.topic() == topic))
                        )
                    (topic, stream)
                })
                .toMap
        }
    }


    import scala.collection.JavaConverters._

    val map = Map[String, Object](
        "bootstrap.servers" -> "hadoop162:9092,hadoop163:9092,hadoop164:9092",
        "key.serializer" -> "org.apache.kafka.common.serialization.StringSerializer",
        "value.serializer" -> "org.apache.kafka.common.serialization.StringSerializer",
        "enable.idempotent" -> (true: java.lang.Boolean)
    )

    def getKafkaProducer() = {
        new KafkaProducer[String, String](map.asJava)
    }

    //向指定的topic写入多条数据
    def sendToKafka(topicAndDatas: Iterator[(String, String)]): Unit = {
        // 创建kafka的生产者
        val producer = getKafkaProducer
        // 写数据
        topicAndDatas.foreach {
            case (topic, data) =>
                new Thread() {
                    override def run(): Unit = {
                        Thread.sleep(new Random().nextInt(1000 * 10))
                        producer.send(new ProducerRecord[String, String](topic, data))
                    }
                }.start()
        }
        // 关闭生产着

        new Thread() {
            override def run(): Unit = {
                Thread.sleep(1000 * 10)
                producer.close()
            }
        }.start()
    }
}

/*
         获取流的各种情况:
             topic的情况:
                 1. 消费一个topic
                 2. 一次消费多个topic
             返回情况(消费多个topic):
                 1. 返回一个流
                 2. 返回多个流: 每个topic对应一个流
      */