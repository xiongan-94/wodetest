package com.atguigu.flink.chapter07;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/25 10:01
 */
public class Flink01_Window_Time_Global {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        env
            .socketTextStream("hadoop162", 9999)
            .flatMap(new FlatMapFunction<String, Tuple2<String, Long>>() {
                // 使用变量
                @Override
                public void flatMap(String value, Collector<Tuple2<String, Long>> out) throws Exception {
                    for (String word : value.split(" ")) {
                        out.collect(Tuple2.of(word, 1L));
                    }
                }
            })
            .keyBy(t -> t.f0)
            //            .window(ProcessingTimeSessionWindows.withGap(Time.seconds(5)))
            .window(GlobalWindows.create())
            .process(new ProcessWindowFunction<Tuple2<String, Long>, String, String, GlobalWindow>() {
                // 使用变量
                @Override
                public void process(String s,
                                    Context context,
                                    Iterable<Tuple2<String, Long>> elements,
                                    Collector<String> out) throws Exception {
                    out.collect(s);

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
