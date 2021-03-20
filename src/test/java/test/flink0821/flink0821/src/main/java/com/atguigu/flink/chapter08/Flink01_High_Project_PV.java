package com.atguigu.flink.chapter08;

import com.atguigu.flink.bean.UserBehavior;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.time.Duration;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/27 14:03
 */
public class Flink01_High_Project_PV {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(3);
        // 滚动窗口长度为1小个小时
        WatermarkStrategy<UserBehavior> wms = WatermarkStrategy
            .<UserBehavior>forBoundedOutOfOrderness(Duration.ofSeconds(5))
            .withTimestampAssigner(new SerializableTimestampAssigner<UserBehavior>() {
                @Override
                public long extractTimestamp(UserBehavior element, long recordTimestamp) {
                    return element.getTimestamp() * 1000;
                }
            });

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
            .filter(b -> "pv".equals(b.getBehavior()))
            .assignTimestampsAndWatermarks(wms)
            .keyBy(UserBehavior::getBehavior)
            .window(TumblingEventTimeWindows.of(Time.hours(1)))
            .aggregate(new AggregateFunction<UserBehavior, Long, Long>() {
                @Override
                public Long createAccumulator() {
                    return 0L;
                }

                @Override
                public Long add(UserBehavior value, Long accumulator) {
                    return accumulator + 1;
                }

                @Override
                public Long getResult(Long accumulator) {
                    return accumulator;
                }

                @Override
                public Long merge(Long a, Long b) {
                    return null;
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
