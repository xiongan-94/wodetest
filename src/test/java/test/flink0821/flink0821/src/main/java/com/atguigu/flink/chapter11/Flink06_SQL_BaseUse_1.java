package com.atguigu.flink.chapter11;

import com.atguigu.flink.bean.WaterSensor;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import static org.apache.flink.table.api.Expressions.$;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/30 15:15
 */
public class Flink06_SQL_BaseUse_1 {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<WaterSensor> waterSensorStream = env
            .fromElements(new WaterSensor("sensor_1", 1000L, 10),
                          new WaterSensor("sensor_1", 2000L, 20),
                          new WaterSensor("sensor_2", 3000L, 30),
                          new WaterSensor("sensor_1", 4000L, 40),
                          new WaterSensor("sensor_1", 5000L, 50),
                          new WaterSensor("sensor_2", 6000L, 60));

        StreamTableEnvironment tenv = StreamTableEnvironment.create(env);
        //        tenv.createTemporaryView("sensor", waterSensorStream);
        // 使用sql查询已注册的表
        tenv.createTemporaryView("sensor", waterSensorStream, $("id").as("a"), $("ts").as("b"), $("vc").as("c"));

        tenv.sqlQuery("select * from  sensor where a='sensor_1'").execute().print();
    }
}
