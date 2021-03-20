package com.atguigu.flink.chapter07;

import com.atguigu.flink.bean.WaterSensor;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.state.AggregatingState;
import org.apache.flink.api.common.state.AggregatingStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/26 16:06
 */
public class Flink13_State_Keyed_AggregateState {
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

                private AggregatingState<Integer, String> state;

                @Override
                public void open(Configuration parameters) throws Exception {
                    state = getRuntimeContext()
                        .getAggregatingState(
                            new AggregatingStateDescriptor<Integer, Tuple2<Integer, Integer>, String>("state", new AggregateFunction<Integer, Tuple2<Integer, Integer>, String>() {
                                @Override
                                public Tuple2<Integer, Integer> createAccumulator() {
                                    System.out.println("createAccumulator...");
                                    // max, max
                                    return Tuple2.of(Integer.MIN_VALUE, Integer.MAX_VALUE);
                                }

                                @Override
                                public Tuple2<Integer, Integer> add(Integer value, Tuple2<Integer, Integer> accumulator) {
                                    System.out.println("add...");
                                    int max = Math.max(value, accumulator.f0);
                                    int min = Math.min(value, accumulator.f1);
                                    return Tuple2.of(max, min);
                                }

                                @Override
                                public String getResult(Tuple2<Integer, Integer> acc) {
                                    System.out.println("getResult...");
                                    return "max=" + acc.f0 + ", min=" + acc.f1;
                                }

                                @Override
                                public Tuple2<Integer, Integer> merge(Tuple2<Integer, Integer> a, Tuple2<Integer, Integer> b) {
                                    System.out.println("merge...");
                                    return Tuple2.of(Math.max(a.f0, b.f0), Math.min(a.f1, b.f1));
                                }
                            }, Types.TUPLE(Types.INT, Types.INT)));
                }

                @Override
                public void processElement(WaterSensor value, Context ctx, Collector<String> out) throws Exception {
                    state.add(value.getVc());

                    out.collect(state.get());

                }
            })
            .print();

        env.execute();
    }
}
