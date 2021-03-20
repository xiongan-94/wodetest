package com.atguigu.flink.chapter06;

import com.atguigu.flink.bean.UserBehavior;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/23 15:10
 */
public class Flink01_Project_PV_1 {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(3);

        env
            .readTextFile("input/UserBehavior.csv")
            .map(line -> {
                String[] data = line.split(",");
                return new UserBehavior(
                    Long.valueOf(data[0]),
                    Long.valueOf(data[1]),
                    Integer.valueOf(data[2]),
                    data[3],
                    Long.valueOf(data[4]));

            })
            .filter(bh -> "pv".equals(bh.getBehavior()))
            .map(bh -> Tuple2.of("pv", 1L)).returns(Types.TUPLE(Types.STRING, Types.LONG))
            .keyBy(t -> t.f0)
            .sum(1)
            .print();

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
