package com.atguigu.flink.chapter11;

import com.atguigu.flink.bean.WaterSensor;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.*;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.time.Duration;

import static org.apache.flink.table.api.Expressions.$;
import static org.apache.flink.table.api.Expressions.lit;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/2/1 9:31
 */
public class Flink10_Table_GroupWindow {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        SingleOutputStreamOperator<WaterSensor> waterSensorStream = env
            .fromElements(new WaterSensor("sensor_1", 1000L, 10),
                          new WaterSensor("sensor_1", 2000L, 20),
                          new WaterSensor("sensor_2", 1000L, 30),
                          new WaterSensor("sensor_1", 4000L, 40),
                          new WaterSensor("sensor_1", 5000L, 50),
                          new WaterSensor("sensor_2", 7000L, 60),
                          new WaterSensor("sensor_2", 16000L, 60))
            .assignTimestampsAndWatermarks(
                WatermarkStrategy
                    .<WaterSensor>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                    .withTimestampAssigner((element, recordTimestamp) -> element.getTs())
            );

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        Table table = tableEnv
            .fromDataStream(waterSensorStream, $("id"), $("ts").rowtime(), $("vc"));

//        TumbleWithSizeOnTimeWithAlias w = Tumble.over(lit(10).second()).on($("ts")).as("w");  // 0-10
//        SlideWithSizeAndSlideOnTimeWithAlias w = Slide.over(lit(10).second()).every(lit(5).second()).on($("ts")).as("w");
        SessionWithGapOnTimeWithAlias w = Session.withGap(lit(5).second()).on($("ts")).as("w");
        table
            .window(w)
            .groupBy($("w"), $("id"))  // group by w,id   窗口必须出现在分组的字段中
            .select($("w").start().as("et_start"), $("w").end().as("et_end"), $("id"), $("vc").sum().as("vcSum"))
            .execute()
            .print();

    }
}
