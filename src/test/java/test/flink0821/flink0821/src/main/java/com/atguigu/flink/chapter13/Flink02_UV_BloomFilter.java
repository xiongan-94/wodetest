package com.atguigu.flink.chapter13;

import com.atguigu.flink.bean.UserBehavior;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.guava18.com.google.common.hash.BloomFilter;
import org.apache.flink.shaded.guava18.com.google.common.hash.Funnels;
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
public class Flink02_UV_BloomFilter {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(3);
        // 滚动窗口长度为1小个小时
        WatermarkStrategy<UserBehavior> wms = WatermarkStrategy
            .<UserBehavior>forBoundedOutOfOrderness(Duration.ofSeconds(5))
            .withTimestampAssigner((element, recordTimestamp) -> element.getTimestamp() * 1000);
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

                private ValueState<Long> countState;
                private ValueState<BloomFilter<Long>> bfState;

                @Override
                public void open(Configuration parameters) throws Exception {
                    // 在状态中存储一个布隆过滤器
                    bfState = getRuntimeContext()
                        .getState(new ValueStateDescriptor<BloomFilter<Long>>(
                                      "bfState",
                                      TypeInformation.of(new TypeHint<BloomFilter<Long>>() {})
                                  )
                        );

                    countState = getRuntimeContext().getState(new ValueStateDescriptor<Long>("countState", Long.class));
                }

                @Override
                public void process(String key,
                                    Context ctx,
                                    Iterable<UserBehavior> elements,
                                    Collector<Long> out) throws Exception {
                    // 布隆过滤器最好一个就够
                    //if (bfState.value() == null) {
                    BloomFilter<Long> bf = BloomFilter.create(Funnels.longFunnel(), 10000000, 0.0001);
                    bfState.update(bf);
                    //}
                    countState.update(0L);

                    for (UserBehavior ele : elements) {
                        if (!bfState.value().mightContain(ele.getUserId())) {
                            countState.update(countState.value() + 1L);
                            bfState.value().put(ele.getUserId());
                        }
                    }
                    out.collect(countState.value());

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
