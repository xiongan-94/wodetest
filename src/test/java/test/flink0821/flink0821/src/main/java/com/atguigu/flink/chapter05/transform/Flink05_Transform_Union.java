package com.atguigu.flink.chapter05.transform;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/22 14:57
 */
public class Flink05_Transform_Union {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        DataStreamSource<Integer> s1 = env
            .fromElements(1, 2, 3, 4);

        DataStreamSource<Integer> s2 = env
            .fromElements(10, 20, 30, 40, 50);

        DataStream<Integer> s3 = s1.union(s2);
        s3.print();

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
