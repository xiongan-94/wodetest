package com.atguigu.flink.chapter08;

import com.atguigu.flink.bean.HotItem;
import com.atguigu.flink.bean.UserBehavior;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.time.Duration;
import java.util.ArrayList;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/23 15:10
 */
public class Flink03_High_Project__Produce_TopN {
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
            // 安装商品id进行分组
            .keyBy(UserBehavior::getItemId)
            .window(SlidingEventTimeWindows.of(Time.hours(1), Time.minutes(5)))
            .aggregate(new AggregateFunction<UserBehavior, Long, Long>() {
                @Override
                public Long createAccumulator() {
                    return 0L;
                }

                @Override
                public Long add(UserBehavior value, Long accumulator) {
                    return accumulator + 1L;
                }

                @Override
                public Long getResult(Long accumulator) {
                    return accumulator;
                }

                @Override
                public Long merge(Long a, Long b) {
                    return null;
                }
            }, new ProcessWindowFunction<Long, HotItem, Long, TimeWindow>() {

                @Override
                public void process(Long key, Context context, Iterable<Long> elements, Collector<HotItem> out) throws Exception {
                    out.collect(new HotItem(key, elements.iterator().next(), context.window().getEnd()));
                }
            })
            .keyBy(HotItem::getWindowEndTime)
            .process(new KeyedProcessFunction<Long, HotItem, String>() {

                private ValueState<Long> timerTS;
                private ListState<HotItem> hotItems;

                @Override
                public void open(Configuration parameters) throws Exception {
                    hotItems = getRuntimeContext().getListState(new ListStateDescriptor<HotItem>("hotItems", HotItem.class));
                    // 存储定时器的触发时间的状态
                    timerTS = getRuntimeContext().getState(new ValueStateDescriptor<Long>("timerTS", Long.class));
                }

                @Override
                public void processElement(HotItem value, Context ctx, Collector<String> out) throws Exception {

                    // 来的数据存储到列表状态中
                    hotItems.add(value);
                    // 注册定时器  以某个结束时间的窗口第一次来的时候注册一个定时器
                    if (timerTS.value() == null) {  //
                        long ts = value.getWindowEndTime() + 1000L;
                        ctx.timerService().registerEventTimeTimer(ts);
                        timerTS.update(ts);

                    }

                }

                @Override
                public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) throws Exception {
                    // 开始排序, 取top3, 发出
                    Iterable<HotItem> hotItems = this.hotItems.get();
                    Long timerTs = this.timerTS.value();

                    ArrayList<HotItem> result = new ArrayList<>();
                    for (HotItem hotItem : hotItems) {
                        result.add(hotItem);
                    }
                    // 排序
                    result.sort((o1, o2) -> o2.getCount().compareTo(o1.getCount()));
                    // topN
                    StringBuilder sb = new StringBuilder();
                    sb.append("窗口结束时间: " + (timestamp - 1000) + "\n");
                    sb.append("---------------------------------\n");
                    for (int i = 0; i < Math.min(3, result.size()); i++) {
                        sb.append(result.get(i) + "\n");
                    }
                    sb.append("---------------------------------\n\n");
                    out.collect(sb.toString());
                    this.hotItems.clear();
                    this.timerTS.clear();
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
/*
统计入门商品
1. 计算出来每个商品被pv的次数
    0-1h
        在这个窗口内进行计算
2. 安装次数降序排列, topN


 */