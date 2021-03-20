package com.atguigu.flink.chapter07;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/23 11:01
 */
public class Flink09_State_Operator_ListState {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        env.setParallelism(2);
        env.enableCheckpointing(1000 * 2);

        env
            .socketTextStream("hadoop162", 9999)
            .map(new MyCounterMapper())
            .print();

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static class MyCounterMapper implements MapFunction<String, Long>, CheckpointedFunction{

        private ListState<Long> state;

        Long count = 0L;

        @Override
        public Long map(String value) throws Exception {
            count ++;
            return count;
        }

        // 初始化状态, 向状态存储数据, 每个子任务调用一次
        @Override
        public void initializeState(FunctionInitializationContext context) throws Exception {
            System.out.println("initializeState...");
//            state = context.getOperatorStateStore().getListState(new ListStateDescriptor<Long>("state", Long.class));
            state = context.getOperatorStateStore().getUnionListState(new ListStateDescriptor<Long>("state", Long.class));

            // 从状态中恢复
            for (Long one : state.get()) {
                count += one;
            }
        }

        // checkpoint的时候会调用这个方法, 实现我们具体的snapshot的具体的逻辑
        @Override
        public void snapshotState(FunctionSnapshotContext context) throws Exception {
            System.out.println("snapshotState ...");
            state.clear();
            state.add(count);
        }
    }
}

