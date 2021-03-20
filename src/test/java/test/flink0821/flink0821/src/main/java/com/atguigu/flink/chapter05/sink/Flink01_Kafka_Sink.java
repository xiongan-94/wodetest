package com.atguigu.flink.chapter05.sink;

import com.alibaba.fastjson.JSON;
import com.atguigu.flink.bean.WaterSensor;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import java.util.ArrayList;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/23 11:01
 */
public class Flink01_Kafka_Sink {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        ArrayList<WaterSensor> waterSensors = new ArrayList<>();
        waterSensors.add(new WaterSensor("sensor_1", 1607527992000L, 20));
        waterSensors.add(new WaterSensor("sensor_1", 1607527994000L, 50));
        waterSensors.add(new WaterSensor("sensor_1", 1607527996000L, 50));
        waterSensors.add(new WaterSensor("sensor_2", 1607527993000L, 10));
        waterSensors.add(new WaterSensor("sensor_2", 1607527995000L, 30));

        env
            .fromCollection(waterSensors)
            .map(JSON::toJSONString)
            .addSink(new FlinkKafkaProducer<String>("hadoop162:9092", "sensor", new SimpleStringSchema()));

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
