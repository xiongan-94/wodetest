package com.atguigu.flink.chapter11;

import com.atguigu.flink.bean.WaterSensor;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import static org.apache.flink.table.api.Expressions.$;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/30 14:25
 */
public class Flink01_Table_BaseUse {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        DataStreamSource<WaterSensor> waterSensorStream = env
            .fromElements(new WaterSensor("sensor_1", 1000L, 10),
                          new WaterSensor("sensor_1", 2000L, 20),
                          new WaterSensor("sensor_2", 3000L, 30),
                          new WaterSensor("sensor_1", 4000L, 40),
                          new WaterSensor("sensor_1", 5000L, 50),
                          new WaterSensor("sensor_2", 6000L, 60));

        // 1. 创建table api使用的环境
        StreamTableEnvironment tenv = StreamTableEnvironment.create(env);
        // 2. 根据流创建动态表
        Table table = tenv.fromDataStream(waterSensorStream);
        // 3. 使用table api操作动态表(查询)
        // select id, vc from .. where id=sensor_1
        Table resultTable = table
            .where($("id").isEqual("sensor_1"))
            .select($("id"), $("ts"), $("vc"));
        // 4. 把查询的结果(动态表)转成流, 最后输出
        DataStream<WaterSensor> resultStream = tenv.toAppendStream(resultTable, WaterSensor.class);
        resultStream.print();
        env.execute();

    }
}
