package com.atguigu.flink.chapter06;

import com.atguigu.flink.bean.UserBehavior;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/23 15:10
 */
public class Flink01_Project_UV {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(3);

        env
            .readTextFile("input/UserBehavior.csv")
            .flatMap(new FlatMapFunction<String, Tuple2<String, Long>>() {
                @Override
                public void flatMap(String value, Collector<Tuple2<String, Long>> out) throws Exception {
                    // 数据封装, 过滤, 变成元组
                    String[] data = value.split(",");
                    UserBehavior ub = new UserBehavior(
                        Long.valueOf(data[0]),
                        Long.valueOf(data[1]),
                        Integer.valueOf(data[2]),
                        data[3],
                        Long.valueOf(data[4]));

                    if ("pv".equals(ub.getBehavior())) {
                        out.collect(Tuple2.of("uv", ub.getUserId()));
                    }
                }
            })
            .keyBy(t -> t.f0)
            .process(new KeyedProcessFunction<String, Tuple2<String, Long>, Integer>() {
                Set<Long> userIds = new HashSet<>();

                @Override
                public void processElement(Tuple2<String, Long> value, Context ctx, Collector<Integer> out) throws Exception {
                    int preSize = userIds.size();
                    userIds.add(value.f1);
                    int postSize = userIds.size();
                    if (postSize > preSize) {
                        out.collect(userIds.size());
                    }
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
