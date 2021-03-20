package com.atguigu.flink.chapter05.transform;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/22 14:57
 */
public class Flink02_Transform_FlatMap {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        DataStreamSource<Integer> s1 = env.fromElements(1, 2, 3, 4);
        s1
            .flatMap((Integer value, Collector<Integer> out) -> {
                out.collect(value);
                out.collect(value * value);
                out.collect(value * value * value);
            }).returns(Types.INT)
            .print();

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
