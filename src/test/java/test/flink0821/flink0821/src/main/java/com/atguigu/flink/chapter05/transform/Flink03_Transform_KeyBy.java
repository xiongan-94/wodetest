package com.atguigu.flink.chapter05.transform;

import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/22 14:57
 */
public class Flink03_Transform_KeyBy {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);
        DataStreamSource<Integer> s1 = env.fromElements(1, 2, 3, 4, 20, 10, 2, 4, 5, 11);

        s1
            .keyBy(new KeySelector<Integer, Integer>() {
                // 传入一个元素,非返回一个key, 来决定刚才传入的这个函数进入后面那个分区
                @Override
                public Integer getKey(Integer value) throws Exception {
                   /* if(value % 2 == 1){
                        return "奇数";
                    }
                    return "偶数";*/
                    return value % 2;
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
/*
new KeyGroupStreamPartitioner<>(keySelector, StreamGraphGenerator.DEFAULT_LOWER_BOUND_MAX_PARALLELISM))
1 << 7


key=0 1  maxParallelism=128  numberOfChannels=2 (key by之后的并行度)
KeyGroupRangeAssignment.assignKeyToParallelOperator(key, maxParallelism, numberOfChannels);

computeOperatorIndexForKeyGroup(maxParallelism, parallelism, assignToKeyGroup(key, maxParallelism));

        computeKeyGroupForKeyHash(key.hashCode(), maxParallelism);  [0,127]

        keyGroupId * parallelism / maxParallelism;  计算出分区索引
 */
