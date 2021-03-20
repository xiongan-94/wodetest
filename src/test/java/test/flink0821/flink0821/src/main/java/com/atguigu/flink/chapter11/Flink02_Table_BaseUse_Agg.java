package com.atguigu.flink.chapter11;

import com.atguigu.flink.bean.WaterSensor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import static org.apache.flink.table.api.Expressions.$;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/30 14:25
 */
public class Flink02_Table_BaseUse_Agg {
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
        // select id, sum(vc) as vc_sum from .. where id=sensor_1 group by id
        Table resultTable = table
            .where($("vc").isGreaterOrEqual(20))
            .groupBy($("id"))
            .aggregate($("vc").sum().as("vc_sum"))
            .select($("id"), $("vc_sum"));

        // 4. 把查询的结果(动态表)转成流, 最后输出
        DataStream<Tuple2<Boolean, Row>> resultStream = tenv.toRetractStream(resultTable, Row.class);
        resultStream.print();
        env.execute();

    }
}
