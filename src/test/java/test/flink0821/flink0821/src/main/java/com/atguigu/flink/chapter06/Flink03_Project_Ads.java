package com.atguigu.flink.chapter06;

import com.atguigu.flink.bean.AdsClickLog;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import static org.apache.flink.api.common.typeinfo.Types.*;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/23 16:04
 */
public class Flink03_Project_Ads {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        env
            .readTextFile("input/AdClickLog.csv")
            .map(line -> {
                String[] datas = line.split(",");
                AdsClickLog log = new AdsClickLog(
                    Long.valueOf(datas[0]),
                    Long.valueOf(datas[1]),
                    datas[2],
                    datas[3],
                    Long.valueOf(datas[4]));

                // ((pro, ads), 1L)
                return Tuple2.of(Tuple2.of(log.getProvince(), log.getAdId()), 1L);
            }).returns(TUPLE(TUPLE(STRING, LONG), LONG))
            .keyBy(new KeySelector<Tuple2<Tuple2<String, Long>, Long>, Tuple2<String, Long>>() {
                @Override
                public Tuple2<String, Long> getKey(Tuple2<Tuple2<String, Long>, Long> value) throws Exception {
                    return value.f0;
                }
            })
            .sum(1)
            .print("省份-广告: 点击量");

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
