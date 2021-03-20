package com.atguigu.flink.chapter11;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/2/1 8:54
 */
public class Flink08_SQL_ProcessTime_SQL {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        // 1. 创建表的执行环境
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        tableEnv.executeSql("create table sensor(id string, ts bigint, vc int, pt as proctime() )" +
                                "with(" +
                                "   'connector'='filesystem', " +
                                "   'path'='input/sensor.txt', " +
                                "   'format'='csv' " +
                                ")");

        tableEnv.executeSql("select * from sensor").print();

    }
}
