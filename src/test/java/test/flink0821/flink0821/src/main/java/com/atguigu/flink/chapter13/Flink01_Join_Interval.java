package com.atguigu.flink.chapter13;

import com.atguigu.flink.bean.WaterSensor;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/2/1 15:08
 */
public class Flink01_Join_Interval {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        env.setParallelism(1);

        SingleOutputStreamOperator<WaterSensor> s1 = env
            .socketTextStream("hadoop162", 8888)  // 在socket终端只输入毫秒级别的时间戳
            .map(value -> {
                String[] datas = value.split(",");
                return new WaterSensor(datas[0], Long.valueOf(datas[1]), Integer.valueOf(datas[2]));

            })
            .assignTimestampsAndWatermarks(
                WatermarkStrategy
                    .<WaterSensor>forMonotonousTimestamps()
                    .withTimestampAssigner(new SerializableTimestampAssigner<WaterSensor>() {
                        @Override
                        public long extractTimestamp(WaterSensor element, long recordTimestamp) {
                            return element.getTs() * 1000;
                        }
                    })
            );

        SingleOutputStreamOperator<WaterSensor> s2 = env
            .socketTextStream("hadoop162", 9999)  // 在socket终端只输入毫秒级别的时间戳
            .map(value -> {
                String[] datas = value.split(",");
                return new WaterSensor(datas[0], Long.valueOf(datas[1]), Integer.valueOf(datas[2]));
            })
            .assignTimestampsAndWatermarks(
                WatermarkStrategy
                    .<WaterSensor>forMonotonousTimestamps()
                    .withTimestampAssigner(new SerializableTimestampAssigner<WaterSensor>() {
                        @Override
                        public long extractTimestamp(WaterSensor element, long recordTimestamp) {
                            return element.getTs() * 1000;
                        }
                    })
            );

        s1
            .keyBy(WaterSensor::getId)
            .intervalJoin(s2.keyBy(WaterSensor::getId))
            .between(Time.seconds(-2), Time.seconds(3))
            .process(new ProcessJoinFunction<WaterSensor, WaterSensor, String>() {
                @Override
                public void processElement(WaterSensor left,
                                           WaterSensor right,
                                           Context ctx, Collector<String> out) throws Exception {
                    out.collect("first=" + left + ", second=" + right);
                }
            })
            .print();

        env.execute();

    }
}
