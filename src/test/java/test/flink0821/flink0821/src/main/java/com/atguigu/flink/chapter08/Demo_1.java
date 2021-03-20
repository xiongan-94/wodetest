package com.atguigu.flink.chapter08;

import com.atguigu.flink.bean.WaterSensor;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.ArrayList;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/27 14:57
 */
public class Demo_1 {
    public static void main(String[] args) throws Exception {
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
            .aggregate(new AggregateFunction<WaterSensor, Long, Long>() {
                @Override
                public Long createAccumulator() {
                    return 0L;
                }

                @Override
                public Long add(WaterSensor value, Long accumulator) {
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
            }, new ProcessWindowFunction<Long, String, String, TimeWindow>() {
                @Override
                public void process(String key, Context context, Iterable<Long> elements, Collector<String> out) throws Exception {
                    ArrayList<Long> longs = new ArrayList<>();
                    for (Long e : elements) {
                        longs.add(e);
                    }
                    System.out.println("process..." + longs);

                    out.collect(key + "   " + longs);
                }
            })
            .print();


        env.execute();
    }
}
