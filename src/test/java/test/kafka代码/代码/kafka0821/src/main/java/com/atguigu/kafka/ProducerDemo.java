package com.atguigu.kafka;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class ProducerDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        //1、创建producer客户端
        Properties props = new Properties();
        //设置key的序列化类型
        props.setProperty("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        //设置value的序列化类
        props.setProperty("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
        //设置ack
        props.setProperty("acks","1");
        //设置每个批次的大小
        props.setProperty("batch.size","1024");
        //设置kafka集群地址
        props.setProperty("bootstrap.servers","hadoop102:9092,hadoop103:9092");

        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);
        //2、封装数据
        for(int i=1;i<=100;i++){
            String messge = "这是发送的第"+i+"条消息";
            ProducerRecord<String, String> record = new ProducerRecord<String, String>("demo", messge);
            System.out.println("开始发送的第"+i+"条消息");
            final int x = i;
            //3、发送 异步
/*            producer.send(record, new Callback() {
                //回调函数是ack返回的时候调用
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    int partition = recordMetadata.partition();
                    long offset = recordMetadata.offset();
                    System.out.println("第"+x+"条消息,partition="+partition+",offset="+offset);
                }
            });*/

            //同步发送
            producer.send(record, new Callback() {
                //回调函数是ack返回的时候调用
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    int partition = recordMetadata.partition();
                    long offset = recordMetadata.offset();
                    System.out.println("第"+x+"条消息,partition="+partition+",offset="+offset);
                }
            }).get();
            System.out.println("第"+i+"条消息发送完成");
        }

        //4、关闭
        producer.close();
    }
}
