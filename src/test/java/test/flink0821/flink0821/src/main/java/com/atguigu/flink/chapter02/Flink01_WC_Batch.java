package com.atguigu.flink.chapter02;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.AggregateOperator;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.FlatMapOperator;
import org.apache.flink.api.java.operators.UnsortedGrouping;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/20 9:56
 */
public class Flink01_WC_Batch {
    public static void main(String[] args) throws Exception {
        // Flink批处理方式

        // 1. 获取执行环境
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        // 2. 从文件获取数据, DataSource  按行读取
        DataSource<String> lineDS = env.readTextFile("input/words.txt");
        // 3. 对DataSource做各种转换
        FlatMapOperator<String, Tuple2<String, Long>> wordAndOne = lineDS.flatMap(new FlatMapFunction<String, Tuple2<String, Long>>() {
            @Override
            public void flatMap(String line, Collector<Tuple2<String, Long>> out) throws Exception {
                String[] words = line.split(" ");
                for (String word : words) {
                    out.collect(Tuple2.of(word, 1L));
                }
            }
        });
        UnsortedGrouping<Tuple2<String, Long>> wordAndOneGrouped = wordAndOne.groupBy(0);
        AggregateOperator<Tuple2<String, Long>> result = wordAndOneGrouped.sum(1);

        // 4. 输出
        result.print();

    }
}
