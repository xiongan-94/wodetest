package com.atguigu.flink.chapter07;

import com.atguigu.flink.bean.WaterSensor;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/26 16:06
 */
public class Flink14_State_Keyed_MapState {
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

                private MapState<Integer, String> state;

                @Override
                public void open(Configuration parameters) throws Exception {
                    state = getRuntimeContext()
                        .getMapState(new MapStateDescriptor<Integer, String>("state", Integer.class, String.class));
                }

                @Override
                public void processElement(WaterSensor value, Context ctx, Collector<String> out) throws Exception {
                    state.put(value.getVc(), "随意");

                    out.collect(state.keys() + "");
                }
            })
            .print();

        env.execute();
    }
}
