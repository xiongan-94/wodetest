package com.atguigu.flink.chapter11;

import com.atguigu.flink.bean.WaterSensor;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.descriptors.Json;
import org.apache.flink.table.descriptors.Kafka;
import org.apache.flink.table.descriptors.Schema;

import static org.apache.flink.table.api.Expressions.$;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/30 15:15
 */
public class Flink05_Connector_Kafka_Sink {
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
        // 输入表
        Table inputStream = tenv
            .fromDataStream(waterSensorStream)
            .where($("id").isEqual("sensor_1"))
            .select($("id"), $("ts"), $("vc"));


        // 输出表 和kafka进行关联
        Schema schema = new Schema()
            .field("id", DataTypes.STRING())
            .field("ts", DataTypes.BIGINT())
            .field("vc", DataTypes.INT());

        tenv
            .connect(new Kafka()
                         .version("universal")
                         .topic("sensor_out")
                         .property("bootstrap.servers", "hadoop162:9092,hadoop163:9092,hadoop164:9092")
                         .sinkPartitionerRoundRobin()
            )
            .withFormat(new Json())
            .withSchema(schema)
            // 每一行就是一个JSON
            .createTemporaryTable("sensor");

        inputStream.executeInsert("sensor");


    }
}
