package com.atguigu.flink.chapter08;

import com.atguigu.flink.bean.UserBehavior;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.time.Duration;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/23 15:10
 */
public class Flink02_High_Project_UV {
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
            .map(new MapFunction<String, UserBehavior>() {
                @Override
                public UserBehavior map(String value) throws Exception {
                    // 数据封装, 过滤, 变成元组
                    String[] data = value.split(",");
                    UserBehavior ub = new UserBehavior(
                        Long.valueOf(data[0]),
                        Long.valueOf(data[1]),
                        Integer.valueOf(data[2]),
                        data[3],
                        Long.valueOf(data[4]));
                    return ub;
                }
            })
            .filter(u -> "pv".equals(u.getBehavior()))
            .assignTimestampsAndWatermarks(wms)
            .keyBy(UserBehavior::getBehavior)
            .window(TumblingEventTimeWindows.of(Time.hours(1)))
            .process(new ProcessWindowFunction<UserBehavior, Long, String, TimeWindow>() {

                private MapState<Long, String> state;

                @Override
                public void open(Configuration parameters) throws Exception {
                    state = getRuntimeContext().getMapState(new MapStateDescriptor<Long, String>("state", Long.class, String.class));
                }

                @Override
                public void process(String key,
                                    Context context,
                                    Iterable<UserBehavior> elements,
                                    Collector<Long> out) throws Exception {
                    // 每个key共用一个状态, 当这个key有多个窗口时候, 多个窗口的 数据会共用状态
                    // 每开始计算一个窗口的时候, 应该清空状态
                    state.clear();

                    for (UserBehavior ub : elements) {
                        state.put(ub.getUserId(), "随意");
                    }

                    // Map中Key (UserId)的个数, 就表示这个窗口的UV
                    Long count = 0L;
                    for (Long aLong : state.keys()) {
                        count++;
                    }
                    System.out.println(context.window());
                    out.collect(count);
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
