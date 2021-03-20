package com.atguigu.flink.chapter11;

import com.atguigu.flink.bean.WaterSensor;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Over;
import org.apache.flink.table.api.OverWindow;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.time.Duration;

import static org.apache.flink.table.api.Expressions.*;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/2/1 9:31
 */
public class Flink12_Table_OverWindow {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        SingleOutputStreamOperator<WaterSensor> waterSensorStream = env
            .fromElements(new WaterSensor("sensor_1", 1000L, 10),
                          new WaterSensor("sensor_1", 2000L, 20),
                          new WaterSensor("sensor_1", 4000L, 40),
                          new WaterSensor("sensor_1", 5000L, 50),
                          new WaterSensor("sensor_2", 1000L, 30),
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

        /*
            select
                id,
                ts,
                sum(vc) over(partition by id order by ts)
           from sensor
         */

//        OverWindow w = Over.partitionBy($("id")).orderBy($("ts")).preceding(UNBOUNDED_ROW).as("w");
//        OverWindow w = Over.partitionBy($("id")).orderBy($("ts")).preceding(UNBOUNDED_RANGE).as("w");

//        OverWindow w = Over.partitionBy($("id")).orderBy($("ts")).preceding(rowInterval(2L)).as("w");
        OverWindow w = Over.partitionBy($("id")).orderBy($("ts")).preceding(lit(2).second()).as("w");

        table
            .window(w)
            .select($("id"), $("ts"), $("vc").sum().over($("w")).as("vc_sum"))
            .execute()
            .print();
    }
}
