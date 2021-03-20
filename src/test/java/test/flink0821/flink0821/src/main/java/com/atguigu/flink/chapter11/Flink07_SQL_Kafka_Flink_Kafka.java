package com.atguigu.flink.chapter11;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/30 15:15
 */
public class Flink07_SQL_Kafka_Flink_Kafka {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        StreamTableEnvironment tenv = StreamTableEnvironment.create(env);

        // 从kafka读数据
        tenv.executeSql("create table source_sensor (id string, ts bigint, vc int) "
                            + "with("
                            + " 'connector' = 'kafka',"
                            + " 'topic' = 'sensor',"
                            + " 'properties.bootstrap.servers' = 'hadoop162:9029,hadoop163:9092,hadoop164:9092',"
                            + " 'properties.group.id' = 'atguigu',"
                            + " 'scan.startup.mode' = 'latest-offset',"
                            + " 'format' = 'json'"
                            + ")");

        //tenv.sqlQuery("select * from source_sensor").execute().print();
        // 向Kafka写数据  输出表和Kafka关联
        tenv.executeSql("create table sink_sensor(id string, ts bigint, vc int)" +
                            " with("
                            + "'connector' = 'kafka',"
                            + "'topic' = 'sensor_out',"
                            + "'properties.bootstrap.servers' = 'hadoop162:9029,hadoop163:9092,hadoop164:9092',"
                            + "'format' = 'json',"
                            + "'sink.partitioner' = 'round-robin'"
                            + ")");

        tenv.executeSql("insert into sink_sensor select * from source_sensor");


    }
}
