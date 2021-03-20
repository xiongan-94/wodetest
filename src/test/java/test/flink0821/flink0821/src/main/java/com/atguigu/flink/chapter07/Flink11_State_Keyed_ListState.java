package com.atguigu.flink.chapter07;

import com.atguigu.flink.bean.WaterSensor;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.util.ArrayList;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/26 16:06
 */
public class Flink11_State_Keyed_ListState {
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

                private ListState<Integer> state;

                @Override
                public void open(Configuration parameters) throws Exception {
                    state = getRuntimeContext().getListState(new ListStateDescriptor<Integer>("state", Integer.class));
                }

                @Override
                public void processElement(WaterSensor value, Context ctx, Collector<String> out) throws Exception {
                    // 状态以后只存3个水位: 最大的三个水平
                    ArrayList<Integer> fourList = new ArrayList<>();
                    for (Integer vc : state.get()) {
                        fourList.add(vc);
                    }
                    fourList.add(value.getVc());

                    // 降序, 取前3, 保存状态中
                    fourList.sort((o1, o2) -> o2 - o1);  // 原地排序

                    if (fourList.size() > 3) {
                        fourList.remove(3);
                    }
                    state.update(fourList);
                    out.collect(ctx.getCurrentKey() + ": " + fourList.toString());
                }
            })
            .print();

        env.execute();
    }
}
