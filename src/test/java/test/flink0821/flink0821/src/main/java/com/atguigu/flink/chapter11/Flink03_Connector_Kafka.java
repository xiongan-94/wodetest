package com.atguigu.flink.chapter11;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.descriptors.Json;
import org.apache.flink.table.descriptors.Kafka;
import org.apache.flink.table.descriptors.Schema;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/30 15:15
 */
public class Flink03_Connector_Kafka {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tenv = StreamTableEnvironment.create(env);

        Schema schema = new Schema()
            .field("id", DataTypes.STRING())
            .field("ts", DataTypes.BIGINT())
            .field("vc", DataTypes.INT());

        tenv
            .connect(new Kafka()
                         .version("universal")
                         .topic("sensor")
                         .startFromLatest()
                         .property("group.id", "bigdata")
                         .property("bootstrap.servers", "hadoop162:9092,hadoop163:9092,hadoop164:9092")
            )
            .withFormat(new Json())
            .withSchema(schema)
            // 每一行就是一个JSON
            .createTemporaryTable("sensor");


        tenv.from("sensor").execute().print();
    }
}
