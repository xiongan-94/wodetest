package com.atguigu.flink.chapter05.source;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Arrays;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/22 11:32
 */
public class Flink01_Source_Collection {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        //        DataStreamSource<Integer> sourceStream = env.fromElements(1, 10, 2, 20, 3, 30);
        // 通常用于测试
        DataStreamSource<Integer> sourceStream = env.fromCollection(Arrays.asList(1, 10, 2, 20, 3, 30));

        sourceStream.print();

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
