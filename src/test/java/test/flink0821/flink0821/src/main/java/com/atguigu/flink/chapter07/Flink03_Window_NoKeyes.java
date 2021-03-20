package com.atguigu.flink.chapter07;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.ArrayList;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/25 10:01
 */
public class Flink03_Window_NoKeyes {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        env.setParallelism(3);

        env
            .socketTextStream("hadoop162", 9999)
            .map(x -> x).setParallelism(2)
            .windowAll(TumblingProcessingTimeWindows.of(Time.seconds(5)))
            .process(new ProcessAllWindowFunction<String, String, TimeWindow>() {
                @Override
                public void process(Context context, Iterable<String> elements, Collector<String> out) throws Exception {

                    ArrayList<String> eles = new ArrayList<>();
                    for (String element : elements) {

                        eles.add(element);
                    }
                    out.collect(eles + ", start=" + context.window().getStart() + ", end=" + context.window().getEnd());
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
