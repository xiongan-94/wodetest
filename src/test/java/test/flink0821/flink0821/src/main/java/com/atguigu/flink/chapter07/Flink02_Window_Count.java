package com.atguigu.flink.chapter07;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/25 10:01
 */
public class Flink02_Window_Count {
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
//            .countWindow(5)
            // 窗口包含5个元素, 每两个元素, 起一个新的窗口
            .countWindow(5, 2)
            .process(new ProcessWindowFunction<Tuple2<String, Long>, String, String, GlobalWindow>() {
                @Override
                public void process(String key,
                                    Context context,
                                    Iterable<Tuple2<String, Long>> elements,
                                    Collector<String> out) throws Exception {
                    int sum =0;
                    for (Tuple2<String, Long> element : elements) {
                        sum += 1;
                    }
                    out.collect("key=" + key + ", size=" + sum);
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
