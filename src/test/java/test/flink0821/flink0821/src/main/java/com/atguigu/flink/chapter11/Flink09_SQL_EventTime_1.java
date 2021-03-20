package com.atguigu.flink.chapter11;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/2/1 8:54
 */
public class Flink09_SQL_EventTime_1 {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 1. 创建表的执行环境
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        tableEnv.executeSql("create table sensor(" +
                                "   id string, " +
                                "   ts bigint, " +
                                "   vc int, " +
                                // ts的单位必须是s
                                "   et as to_timestamp(from_unixtime(ts,'yyyy-MM-dd HH:mm:ss')), " +
                                "   watermark for et as et - interval '5' second" +
                                ")" +
                                "with(" +
                                "   'connector'='filesystem', " +
                                "   'path'='input/sensor.txt', " +
                                "   'format'='csv' " +
                                ")");

        tableEnv.sqlQuery("select * from sensor").execute().print();

    }
}
