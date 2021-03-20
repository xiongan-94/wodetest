/*
package com.atguigu.flink.chapter05.transform;

import org.apache.flink.streaming.api.collector.selector.OutputSelector;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SplitStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Collections;

*/
/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/22 16:28
 *//*

public class Flink04_Transform_Split {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        DataStreamSource<Integer> s1 = env.fromElements(1, 2, 3, 4, 5, 6);

        SplitStream<Integer> splitStream = s1.split(new OutputSelector<Integer>() {
            @Override
            public Iterable<String> select(Integer value) {
                return value % 2 == 0 ? Collections.singletonList("偶数") : Collections.singletonList("奇数");
            }
        });

        splitStream.select("偶数").print("偶数");
        splitStream.select("奇数").print("奇数");

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
*/
