package com.atguigu.flink.chapter04;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/22 10:09
 */
public class Flink01_Operator_Chain {
    public static void main(String[] args) throws Exception {
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
       // env.setParallelism(2);
       // env.disableOperatorChaining();

        DataStreamSource<String> s1 = env.socketTextStream("hadoop162", 9999);

        SingleOutputStreamOperator<String> s2 = s1.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String value, Collector<String> out) throws Exception {
                for (String word : value.split(" ")) {
                    out.collect(word);
                }
            }
        });

        SingleOutputStreamOperator<Tuple2<String, Long>> s3 = s2.map(new MapFunction<String, Tuple2<String, Long>>() {
            @Override
            public Tuple2<String, Long> map(String word) throws Exception {
                return Tuple2.of(word, 1L);
            }
        });

        KeyedStream<Tuple2<String, Long>, String> s4 = s3.keyBy(t -> t.f0);

        s4.sum(1).print();

        env.execute();
    }
}
/*
修改并行度:
1. 配置文件中修改
2. 提交的参数中指定
3. env 中设置
4. 在算中设置
---

.startNewChain() 从当前算子开始起一个新的任务链

.disableChaining() 当前算子不参与任何的任务链

env.disableOperatorChaining(); 整个job禁止任务链
 */