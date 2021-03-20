package com.atguigu.flink.chapter11;

import com.atguigu.flink.bean.WaterSensor;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import static org.apache.flink.table.api.Expressions.$;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/2/1 8:54
 */
public class Flink08_SQL_ProcessTime {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        DataStreamSource<WaterSensor> waterSensorStream =
            env.fromElements(new WaterSensor("sensor_1", 1000L, 10),
                             new WaterSensor("sensor_1", 2000L, 20),
                             new WaterSensor("sensor_2", 3000L, 30),
                             new WaterSensor("sensor_1", 4000L, 40),
                             new WaterSensor("sensor_1", 5000L, 50),
                             new WaterSensor("sensor_2", 6000L, 60));
        // 1. 创建表的执行环境
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        // 处理时间这一列,不能是已有字段, 只能添加新的一列到最后
        Table table = tableEnv.fromDataStream(waterSensorStream, $("id"), $("ts"), $("vc"), $("pt").proctime());
        table.execute().print();
    }
}
