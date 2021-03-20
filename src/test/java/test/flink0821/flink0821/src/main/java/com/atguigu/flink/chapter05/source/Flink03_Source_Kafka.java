package com.atguigu.flink.chapter05.source;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Properties;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/22 11:32
 */
public class Flink03_Source_Kafka {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "hadoop162:9092,hadoop163:9092,hadoop164:9092");
        props.setProperty("group.id", "Flink03_Source_Kafka");
        props.setProperty("auto.offset.reset", "latest");
        DataStreamSource<String> steam = env.addSource(new FlinkKafkaConsumer<>("sensor", new SimpleStringSchema(), props));
//        DataStreamSource<ObjectNode> sensor = env
//            .addSource(new FlinkKafkaConsumer<>("sensor", new JSONKeyValueDeserializationSchema(false), props));

        steam.print();
        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
