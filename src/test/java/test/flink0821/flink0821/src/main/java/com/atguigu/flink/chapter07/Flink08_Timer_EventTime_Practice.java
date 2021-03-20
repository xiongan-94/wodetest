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
public class Flink08_Timer_EventTime_Practice {
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
                private long time;
                boolean isFirst = true;
                int lastVc = 0;  // 上一次的水位值
                @Override
                public void processElement(WaterSensor value, Context ctx, Collector<String> out) throws Exception {
                    if(value.getVc() > lastVc){
                        System.out.println("水位上升...");
                        if(isFirst){
                            time = ctx.timestamp() + 5000L;
                            System.out.println("注册定时器: " + time);
                            // 注册定时器
                            ctx.timerService().registerEventTimeTimer(time);
                            isFirst = false;
                        }
                    }else{
                        System.out.println("水位下降...");
                        ctx.timerService().deleteEventTimeTimer(time);
                        time = ctx.timestamp() + 5000L;
                        System.out.println("注册定时器..." + time);
                        ctx.timerService().registerEventTimeTimer(time);
                    }
                    lastVc = value.getVc();
                }

                @Override
                public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) throws Exception {
                    out.collect("水位连续5s上升预警预警!!!!");
                    isFirst = true;  // 开启一个新的定时器
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

