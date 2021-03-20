package com.atguigu.flink.chapter08;

import com.atguigu.flink.bean.AdsClickLog;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/29 9:20
 */
public class Flink06_High_Project_BlackList {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);
        // 创建WatermarkStrategy
        WatermarkStrategy<AdsClickLog> wms = WatermarkStrategy
            .<AdsClickLog>forBoundedOutOfOrderness(Duration.ofSeconds(20))
            .withTimestampAssigner(new SerializableTimestampAssigner<AdsClickLog>() {
                @Override
                public long extractTimestamp(AdsClickLog element, long recordTimestamp) {
                    return element.getTimestamp() * 1000L;
                }
            });
        SingleOutputStreamOperator<String> result = env
            .readTextFile("input/AdClickLog.csv")
            .map(line -> {
                String[] datas = line.split(",");
                return new AdsClickLog(Long.valueOf(datas[0]),
                                       Long.valueOf(datas[1]),
                                       datas[2],
                                       datas[3],
                                       Long.valueOf(datas[4]));
            })
            .assignTimestampsAndWatermarks(wms)
            // 按照 (用户, 广告) 分组
            .keyBy(new KeySelector<AdsClickLog, Tuple2<Long, Long>>() {
                @Override
                public Tuple2<Long, Long> getKey(AdsClickLog log) throws Exception {
                    return Tuple2.of(log.getUserId(), log.getAdId());
                }
            })
            .process(new KeyedProcessFunction<Tuple2<Long, Long>, AdsClickLog, String>() {
                // 表示这个用户针对某个广播是否被警告过
                private ValueState<Boolean> warned;

                private ValueState<Long> clickCountState;

                @Override
                public void open(Configuration parameters) throws Exception {
                    clickCountState = getRuntimeContext().getState(new ValueStateDescriptor<Long>("clickCountState", Long.class));
                    warned = getRuntimeContext().getState(new ValueStateDescriptor<Boolean>("warned", Boolean.class));
                }

                // 黑名单的阈值是: 100
                @Override
                public void processElement(AdsClickLog log, Context ctx, Collector<String> out) throws Exception {
                    // 聚合
                    if (clickCountState.value() == null) {
                        clickCountState.update(1L);
                        out.collect("用户: " + log.getUserId()
                                        + ", 广告: " + log.getAdId()
                                        + " 的点击量是: " + clickCountState.value());

                        // 注册一个定时器:
                        // 计算明天的0:0:0
                        Long now = ctx.timestamp();
                        LocalDateTime today = LocalDateTime.ofEpochSecond(now / 1000, 0, ZoneOffset.ofHours(8));
                        LocalDateTime tomorrow = LocalDateTime.of(today.toLocalDate().plusDays(1), LocalTime.of(0, 0, 0));

                        ctx.timerService().registerEventTimeTimer(tomorrow.toEpochSecond(ZoneOffset.ofHours(8)));

                    } else if (clickCountState.value() < 99) {
                        clickCountState.update(clickCountState.value() + 1L);
                        out.collect("用户: " + log.getUserId()
                                        + ", 广告: " + log.getAdId()
                                        + " 的点击量是: " + clickCountState.value());

                    } else {
                        // 进入黑名单, 黑名单用户的点击数据不再处理, 只需要发出警告, 每个用户每天只警告一次
                        if (warned.value() == null) {
                            String msg = "用户: " + log.getUserId()
                                + ", 广告: " + log.getAdId()
                                + " 的点击量是: 99+";

                            ctx.output(new OutputTag<String>("警告流") {}, msg);
                            warned.update(true);

                        }
                    }
                }

                @Override
                public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) throws Exception {
                    // 清空状态
                    warned.clear();
                    clickCountState.clear();
                }
            });

        result.print("正常流");

        result.getSideOutput(new OutputTag<String>("警告流") {}).print("警告流");

        env.execute();

    }
}
