package com.atguigu.flink.chapter11;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/2/1 9:31
 */
public class Flink12_SQL_OverWindow {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);
        // 作为事件时间的字段必须是 timestamp 类型, 所以根据 long 类型的 ts 计算出来一个 t
        tEnv.executeSql("create table sensor(" +
                            "id string," +
                            "ts bigint," +
                            "vc int, " +
                            "t as to_timestamp(from_unixtime(ts,'yyyy-MM-dd HH:mm:ss'))," +
                            "watermark for t as t - interval '5' second)" +
                            "with("
                            + "'connector' = 'filesystem',"
                            + "'path' = 'input/sensor.txt',"
                            + "'format' = 'csv'"
                            + ")");

        /*tEnv.sqlQuery("select id, ts, " +
                          " sum(vc) over(partition by id order by t)" +
                          "from sensor")
            .execute()
            .print();*/

        /*tEnv.sqlQuery("select id, ts, " +
//                          " sum(vc) over(partition by id order by t rows between unbounded preceding and current row)" +
                          " sum(vc) over(partition by id order by t rows between 1 preceding and current row), " +
                          " max(vc) over(partition by id order by t rows between 1 preceding and current row)" +
                          "from sensor")
            .execute()
            .print();
*/
        tEnv.sqlQuery("select id, ts, " +
                          //                          " sum(vc) over(partition by id order by t rows between unbounded preceding and current row)" +
                          " sum(vc) over w, " +
                          " max(vc) over w " +
                          "from sensor " +
                          "window w as (partition by id order by t rows between 1 preceding and current row)")
            .execute()
            .print();

    }
}
