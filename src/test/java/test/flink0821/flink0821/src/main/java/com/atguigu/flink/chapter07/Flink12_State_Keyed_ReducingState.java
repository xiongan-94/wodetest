package com.atguigu.flink.chapter07;

import com.atguigu.flink.bean.WaterSensor;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.state.ReducingState;
import org.apache.flink.api.common.state.ReducingStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/26 16:06
 */
public class Flink12_State_Keyed_ReducingState {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment
            .getExecutionEnvironment()
            .setParallelism(3);
        env
            .socketTextStream("hadoop162", 9999)
            .map(value -> {
                String[] datas = value.split(",");
                return new WaterSensor(datas[0], Long.valueOf(datas[1]), Integer.valueOf(datas[2]));

            })
            .keyBy(WaterSensor::getId)
            .process(new KeyedProcessFunction<String, WaterSensor, String>() {

                private ReducingState<Integer> state;

                @Override
                public void open(Configuration parameters) throws Exception {
                    state = getRuntimeContext().getReducingState(new ReducingStateDescriptor<Integer>("state", new ReduceFunction<Integer>() {
                        @Override
                        public Integer reduce(Integer value1, Integer value2) throws Exception {
                            return Math.max(value1, value2);
                        }
                    }, Integer.class));
                }

                @Override
                public void processElement(WaterSensor value, Context ctx, Collector<String> out) throws Exception {
                    state.add(value.getVc());
                    out.collect(ctx.getCurrentKey() + "的最高水位:" + state.get());
                }
            })
            .print();

        env.execute();
    }
}
