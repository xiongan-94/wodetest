package com.atguigu.flink.chapter07;

import com.atguigu.flink.bean.WaterSensor;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.time.Duration;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/23 11:01
 */
public class Flink07_WM_Sideoutput {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        env.setParallelism(1);

        WatermarkStrategy<WaterSensor> wms = WatermarkStrategy
            .<WaterSensor>forBoundedOutOfOrderness(Duration.ofSeconds(3))
            .withTimestampAssigner(new SerializableTimestampAssigner<WaterSensor>() {
                @Override
                public long extractTimestamp(WaterSensor element, long recordTimestamp) {
                    return element.getTs() * 1000;  // 返回event_time
                }
            });

        SingleOutputStreamOperator<String> result = env
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
            // 如果时间到了, 则开始对窗口内的数据进行计算输出, 窗口并不会关闭, 2s之后才会关
            .allowedLateness(Time.seconds(2))
            .sideOutputLateData(new OutputTag<WaterSensor>("sd"){})
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
            });

        result.getSideOutput(new OutputTag<WaterSensor>("sd"){}).print("side");
        result.print("main");

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
/*
Flink对迟到数据的处理: 针对event time
    1. 水印
    2. 允许迟到数据
        时间到了之后, 先对窗口中的数据进行计算输出, 窗口不关闭,
        后面来的迟到的数据还可以进入这个窗口
    3. 侧输出流
        真正迟到的数据会自动进入侧输出流

 */
