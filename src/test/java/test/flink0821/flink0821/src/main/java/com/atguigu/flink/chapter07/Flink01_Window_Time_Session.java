package com.atguigu.flink.chapter07;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.ProcessingTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.assigners.SessionWindowTimeGapExtractor;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.Date;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/25 10:01
 */
public class Flink01_Window_Time_Session {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        env
            .socketTextStream("hadoop162", 9999)
            .flatMap(new FlatMapFunction<String, Tuple2<String, Long>>() {
                @Override
                public void flatMap(String value, Collector<Tuple2<String, Long>> out) throws Exception {
                    for (String word : value.split(" ")) {
                        out.collect(Tuple2.of(word, 1L));
                    }
                }
            })
            .keyBy(t -> t.f0)
//            .window(ProcessingTimeSessionWindows.withGap(Time.seconds(5)))
            .window(ProcessingTimeSessionWindows.withDynamicGap(new SessionWindowTimeGapExtractor<Tuple2<String, Long>>() {
                @Override
                public long extract(Tuple2<String, Long> element) {
                    return element.f0.length() * 1000;
                }
            }))
            .process(new ProcessWindowFunction<Tuple2<String, Long>, String, String, TimeWindow>() {
                @Override
                public void process(String key,
                                    Context context,
                                    Iterable<Tuple2<String, Long>> elements,
                                    Collector<String> out) throws Exception {
                    long start = context.window().getStart();
                    long end = context.window().getEnd();
                    out.collect("key = " + key + ", start = " + new Date(start) +", end = " + new Date(end));
                }
            })
            .print();

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
