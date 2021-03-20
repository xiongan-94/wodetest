package com.atguigu.flink.chapter02;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/20 10:29
 */
public class Flink02_WC_BoundedStream_Lambda {
    public static void main(String[] args) throws Exception {
        // 1. 先创建一个流式的环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        // 2. 从文件获取一个有界流
        DataStreamSource<String> sourceDS = env.readTextFile("input/words.txt");
        // 3. 各种转换
        SingleOutputStreamOperator<String> wordStream = sourceDS.flatMap((String value, Collector<String> out) -> {
            for (String word : value.split(" ")) {
                out.collect(word);
            }
        }).returns(Types.STRING);
        SingleOutputStreamOperator<Tuple2<String, Long>> wordAndOneStream = wordStream
            .map( (String word) -> Tuple2.of(word, 1L))
            .returns(Types.TUPLE(Types.STRING, Types.LONG));

//        KeyedStream<Tuple2<String, Long>, Tuple> wordAndOneKeyed = wordAndOneStream.keyBy(0);
        KeyedStream<Tuple2<String, Long>, String> wordAndOneKS = wordAndOneStream
            .keyBy( value -> value.f0);
        SingleOutputStreamOperator<Tuple2<String, Long>> result = wordAndOneKS.sum(1);
        // 4. 输出
        result.print();
        // 5. 执行流式的环境
        env.execute();
    }
}
