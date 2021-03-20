package com.atguigu.flink.chapter07;

import com.atguigu.flink.bean.WaterSensor;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.time.Duration;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/23 11:01
 */
public class Flink08_Timer_EventTime {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        env.setParallelism(1);

        env
            .socketTextStream("hadoop162", 9999)
            .map(line -> {
                String[] split = line.split(",");
                WaterSensor waterSensor = new WaterSensor(split[0],
                                                          Long.valueOf(split[1]),
                                                          Integer.valueOf(split[2]));
                return waterSensor;

            })
            .assignTimestampsAndWatermarks(WatermarkStrategy
                                               .<WaterSensor>forBoundedOutOfOrderness(Duration.ofSeconds(3))
                                               .withTimestampAssigner(new SerializableTimestampAssigner<WaterSensor>() {
                                                   @Override
                                                   public long extractTimestamp(WaterSensor element, long recordTimestamp) {
                                                       return element.getTs() * 1000;
                                                   }
                                               }))
            .keyBy(WaterSensor::getId)
            .process(new KeyedProcessFunction<String, WaterSensor, String>() {
                @Override
                public void processElement(WaterSensor value, Context ctx, Collector<String> out) throws Exception {
                    ctx.timerService().registerEventTimeTimer(value.getTs() * 1000 + 5000);
                }

                @Override
                public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) throws Exception {
                    System.out.println(timestamp);
                    out.collect("定时器被触发了....");
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

