package com.atguigu.flink.chapter11;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.catalog.hive.HiveCatalog;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/2/1 11:30
 */
public class Flink13_SQL_Hive {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);

        String name = "hive";
        String defaultDB = "my_flink";
        String hiveConfDir = "input";

        // 1. 创建HiveCatalog
        HiveCatalog hiveCatalog = new HiveCatalog(name, defaultDB, hiveConfDir);
        // 2. 注册HiveCatalog
        tEnv.registerCatalog(name, hiveCatalog);
        // 3. 把hiveCatalog设置为默认的
        tEnv.useCatalog(name);
        tEnv.useDatabase(defaultDB);

        tEnv.sqlQuery("select * from person").execute().print();

    }
}
