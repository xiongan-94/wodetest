package com.atguigu.flink.chapter11;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/2/1 9:31
 */
public class Flink11_SQL_GroupWindow {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        tableEnv.executeSql("create table sensor(" +
                                "id string," +
                                "ts bigint," +
                                "vc int, " +
                                "t as to_timestamp(from_unixtime(ts/1000,'yyyy-MM-dd HH:mm:ss'))," +
                                "watermark for t as t - interval '5' second)" +
                                "with("
                                + "'connector' = 'filesystem',"
                                + "'path' = 'input/sensor.txt',"
                                + "'format' = 'csv'"
                                + ")");

        tableEnv
            .sqlQuery("select  " +
                          " id, " +
                          " tumble_start(t, interval '1' minute), " +
                          " tumble_end(t, interval '1' minute), " +
                          " sum(vc) vc_sum " +
                          "from sensor " +
                          "group by TUMBLE(t, interval '1' minute), id")
            .execute()
            .print();

    }
}
