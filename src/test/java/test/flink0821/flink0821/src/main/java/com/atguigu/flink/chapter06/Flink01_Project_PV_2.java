package com.atguigu.flink.chapter06;

import com.atguigu.flink.bean.UserBehavior;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/23 15:10
 */
public class Flink01_Project_PV_2 {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(3);

        env
            .readTextFile("input/UserBehavior.csv")
            .map(line -> {
                String[] data = line.split(",");
                return new UserBehavior(
                    Long.valueOf(data[0]),
                    Long.valueOf(data[1]),
                    Integer.valueOf(data[2]),
                    data[3],
                    Long.valueOf(data[4]));

            })
            .keyBy(UserBehavior::getBehavior)
            .process(new KeyedProcessFunction<String, UserBehavior, String>() {
                Long pvSum = 0L;

                @Override
                public void processElement(UserBehavior value, Context ctx, Collector<String> out) throws Exception {
                    if ("pv".equals(value.getBehavior())) {
                        pvSum += 1L;
                        out.collect(pvSum + "");
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
