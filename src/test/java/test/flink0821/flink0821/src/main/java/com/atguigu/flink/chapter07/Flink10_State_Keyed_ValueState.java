package com.atguigu.flink.chapter07;

import com.atguigu.flink.bean.WaterSensor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/26 16:06
 */
public class Flink10_State_Keyed_ValueState {
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

                private ValueState<Integer> valueState;

                @Override
                public void open(Configuration parameters) throws Exception {
                    // 获取状态对象
                    valueState = getRuntimeContext().getState(new ValueStateDescriptor<Integer>("valueState", Integer.class));
                }

                @Override
                public void processElement(WaterSensor value, Context ctx, Collector<String> out) throws Exception {
                    // 上一个水位在状态中存储
                    Integer lastVc = valueState.value();
                    if(lastVc != null){
                        if(value.getVc() - lastVc > 10){
                            out.collect("水位预警, 请注意!!!!");
                        }
                    }

                    // 这次的水位都要存到状态, 以备下次使用
                    valueState.update(value.getVc());

                }
            })
            .print();

        env.execute();
    }
}
