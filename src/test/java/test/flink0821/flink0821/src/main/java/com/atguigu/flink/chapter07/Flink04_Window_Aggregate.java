package com.atguigu.flink.chapter07;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/25 10:01
 */
public class Flink04_Window_Aggregate {
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
            .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
            .aggregate(new AggregateFunction<Tuple2<String, Long>, Long, String>() {
                // 创建累加器: 初始化累加器.  这个窗口的第一个元素进来的时候的调用
                @Override
                public Long createAccumulator() {
                    System.out.println("createAccumulator...");
                    return 0L;
                }
                // 累加  这个窗口每进一个元素, 执行一次
                @Override
                public Long add(Tuple2<String, Long> value, Long accumulator) {
                    System.out.println("add...");
                    return accumulator + 1L;
                }
                // 获取最终的累加值. 当窗口关闭的时候调用
                @Override
                public String getResult(Long accumulator) {
                    System.out.println("getResult...");
                    return "acc: " + accumulator;
                }
                // 合并累加器  只有当是会话窗口的时候才会执行. 可以不实现
                @Override
                public Long merge(Long acc1, Long acc2) {
                    System.out.println("merge...");
                    return acc1 + acc2;
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
