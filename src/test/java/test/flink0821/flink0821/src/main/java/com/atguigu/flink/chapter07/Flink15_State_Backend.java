package com.atguigu.flink.chapter07;

import com.atguigu.flink.bean.WaterSensor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.contrib.streaming.state.RocksDBStateBackend;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/27 9:08
 */
public class Flink15_State_Backend {
    public static void main(String[] args) throws Exception {
        System.setProperty("HADOOP_USER_NAME", "atguigu");
        StreamExecutionEnvironment env = StreamExecutionEnvironment
            .getExecutionEnvironment()
            .setParallelism(3);
        // 配置内存式的状态后端
        //        env.setStateBackend(new MemoryStateBackend());
       // env.setStateBackend(new FsStateBackend("hdfs://hadoop162:8020/flink/ck/fs"));
        env.setStateBackend(new RocksDBStateBackend("hdfs://hadoop162:8020/flink/ck/rocksdb"));
        env.enableCheckpointing(1000);

        env
            .socketTextStream("hadoop162", 9999)
            .map(value -> {
                String[] datas = value.split(",");
                return new WaterSensor(datas[0], Long.valueOf(datas[1]), Integer.valueOf(datas[2]));

            })
            .keyBy(WaterSensor::getId)
            .process(new KeyedProcessFunction<String, WaterSensor, String>() {

                private ValueState<Long> state;

                @Override
                public void open(Configuration parameters) throws Exception {
                    state = getRuntimeContext().getState(new ValueStateDescriptor<Long>("state", Long.class));
                }

                @Override
                public void processElement(WaterSensor value, Context ctx, Collector<String> out) throws Exception {
                    Long count = state.value();
                    state.update((count == null ? 0L : count) + 1);

                    out.collect(ctx.getCurrentKey() + "  " + state.value());

                }
            })
            .print();

        env.execute();
    }
}
