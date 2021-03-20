package com.atguigu.flink.chapter05.transform;

import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/22 14:57
 */
public class Flink08_Transform_Rebalance {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        env
            .fromElements(10, 20, 30, 40, 50, 60, 70, 80)
            .keyBy(new KeySelector<Integer, String>() {
                @Override
                public String getKey(Integer value) throws Exception {
                    return value % 2 == 0 ? "偶数" : "奇数";
                }
            })
            .map(x -> x)
            .rebalance()
            .print();

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
