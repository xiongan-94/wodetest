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

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/23 11:01
 */
public class Flink05_WM_Order {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        env.setParallelism(1);

        WatermarkStrategy<WaterSensor> wms = WatermarkStrategy
            .<WaterSensor>forMonotonousTimestamps()
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
                WaterSensor waterSensor = new WaterSensor(split[0], Long.valueOf(split[1]), Integer.valueOf(split[2]));
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
