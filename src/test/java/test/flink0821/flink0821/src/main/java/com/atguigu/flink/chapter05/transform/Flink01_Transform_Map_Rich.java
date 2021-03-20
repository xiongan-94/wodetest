package com.atguigu.flink.chapter05.transform;

import com.atguigu.flink.bean.WaterSensor;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/22 14:57
 */
public class Flink01_Transform_Map_Rich {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);
        DataStreamSource<String> lineStream = env.readTextFile("input/sensor.txt");
        lineStream
            .map(new RichMapFunction<String, WaterSensor>() {
                @Override
                public void open(Configuration parameters) throws Exception {
                    // 建立到外部的连接.  每个并行度执行一次
                    System.out.println("open ....");
                }

                @Override
                public WaterSensor map(String line) throws Exception {
                    System.out.println("一个元素执行一次");
                    String[] data = line.split(",");
                    return new WaterSensor(data[0], Long.valueOf(data[1]), Integer.valueOf(data[2]));
                }

                @Override
                public void close() throws Exception {
                    System.out.println("close ....");
                }
            }).setParallelism(1)
            .print();

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
