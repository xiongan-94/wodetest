package com.atguigu.flink.chapter09;

import com.atguigu.flink.bean.WaterSensor;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.List;
import java.util.Map;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/29 15:26
 */
public class Flink05_CEP_GroupPattern {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        env.setParallelism(1);

        WatermarkStrategy<WaterSensor> wms = WatermarkStrategy
            .<WaterSensor>forMonotonousTimestamps()
            .withTimestampAssigner(new SerializableTimestampAssigner<WaterSensor>() {
                @Override
                public long extractTimestamp(WaterSensor element, long recordTimestamp) {
                    return element.getTs() * 1000;
                }
            });

        SingleOutputStreamOperator<WaterSensor> waterSensorStream = env
            //.socketTextStream("hadoop162", 9999)
            .readTextFile("input/sensor.txt")
            .map(new MapFunction<String, WaterSensor>() {
                @Override
                public WaterSensor map(String line) throws Exception {
                    String[] data = line.split(",");
                    return new WaterSensor(data[0], Long.valueOf(data[1]), Integer.valueOf(data[2]));
                }
            })
            .assignTimestampsAndWatermarks(wms);

        // 1. 定义模式  (定义正则表达式)
        Pattern<WaterSensor, WaterSensor> startPattern = Pattern
            .begin(Pattern
                       .<WaterSensor>begin("start")
                       .where(new SimpleCondition<WaterSensor>() {
                           @Override
                           public boolean filter(WaterSensor value) throws Exception {
                               return value.getId().equals("sensor_1");
                           }
                       })
                       .next("end")
                       .where(new SimpleCondition<WaterSensor>() {
                           @Override
                           public boolean filter(WaterSensor value) throws Exception {
                               return value.getId().equals("sensor_2");
                           }
                       }))
            .times(2).consecutive();

        // 2. 把模式运用在数据流上 (在字符串上使用正则  string.matches(regex))
        PatternStream<WaterSensor> patternStream = CEP.pattern(waterSensorStream, startPattern);

        // 3. 获取满足模式的匹配到的数据
        patternStream
            .select(new PatternSelectFunction<WaterSensor, String>() {
                @Override
                public String select(Map<String, List<WaterSensor>> pattern) throws Exception {
                    return pattern.toString();
                }
            })
            .print();

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
/*
a{2}
a{2,}
a{2,4}
a*
a+
a?

 */