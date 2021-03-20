package com.atguigu.flink.chapter07;

import com.atguigu.flink.bean.WaterSensor;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
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
 * @Date 2021/1/23 11:01
 */
public class Flink05_WM_OutOrder {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        env.setParallelism(2);

        WatermarkStrategy<WaterSensor> wms = WatermarkStrategy
            .<WaterSensor>forBoundedOutOfOrderness(Duration.ofSeconds(3))
            .withTimestampAssigner(new SerializableTimestampAssigner<WaterSensor>() {
                @Override
                public long extractTimestamp(WaterSensor element, long recordTimestamp) {
                    return element.getTs() * 1000;  // 返回event_time
                }
            });

        env
            .socketTextStream("hadoop162", 9999)
            .map(line -> {
                String[] split = line.split(",");
                WaterSensor waterSensor = new WaterSensor(split[0],
                                                          Long.valueOf(split[1]),
                                                          Integer.valueOf(split[2]));
                return waterSensor;

            })
            .assignTimestampsAndWatermarks(wms)
            .keyBy(WaterSensor::getId)
            .window(TumblingEventTimeWindows.of(Time.seconds(5)))
            .process(new ProcessWindowFunction<WaterSensor, String, String, TimeWindow>() {
                @Override
                public void process(String key, Context context, Iterable<WaterSensor> elements, Collector<String> out) throws Exception {
                    int sum = 0;
                    for (WaterSensor element : elements) {
                        sum++;
                    }
                    out.collect("key=" + key +
                                    ", window=[" + context.window().getStart() + "," + context.window().getEnd() + ")" +
                                    ", 元素个数=" + sum);
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
1. 术印可以看成是插入到流中的特殊数据: 只有时间戳
2. 水印只会增长不会降低(时间不会倒流)
    通过找最大时间戳来实现 max
3. 一旦水印倒流 t 就意味着时间小于t的数据不会再处理

 */
