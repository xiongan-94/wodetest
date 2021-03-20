package com.atguigu.flink.chapter05.transform;

import com.atguigu.flink.bean.WaterSensor;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/22 14:57
 */
public class Flink01_Transform_Map {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        DataStreamSource<String> lineStream = env.readTextFile("input/sensor.txt");
        lineStream
            .map(new MapFunction<String, WaterSensor>() {
                @Override
                public WaterSensor map(String line) throws Exception {
                    String[] data = line.split(",");
                    return new WaterSensor(data[0], Long.valueOf(data[1]), Integer.valueOf(data[2]));
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
