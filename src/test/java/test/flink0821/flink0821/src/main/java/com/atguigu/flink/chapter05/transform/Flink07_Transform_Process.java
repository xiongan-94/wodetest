package com.atguigu.flink.chapter05.transform;

import com.atguigu.flink.bean.WaterSensor;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/22 14:57
 */
public class Flink07_Transform_Process {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        ArrayList<WaterSensor> waterSensors = new ArrayList<>();
        waterSensors.add(new WaterSensor("sensor_1", 1L, 20));
        waterSensors.add(new WaterSensor("sensor_2", 4L, 10));
        waterSensors.add(new WaterSensor("sensor_1", 2L, 50));
        waterSensors.add(new WaterSensor("sensor_1", 3L, 50));
        waterSensors.add(new WaterSensor("sensor_2", 5L, 30));

        /*env
            .fromCollection(waterSensors)
            .process(new ProcessFunction<WaterSensor, Integer>() {
                Integer sum = 0;
                @Override
                public void processElement(WaterSensor value, Context ctx, Collector<Integer> out) throws Exception {
                    sum += value.getVc();
                    out.collect(sum);
                }
            })
            .print();*/

        env
            .fromCollection(waterSensors)
            .keyBy(WaterSensor::getId)
            .process(new KeyedProcessFunction<String, WaterSensor, String>() {
               Map<String, Integer> sumMap = new HashMap<String, Integer>();
                @Override
                public void processElement(WaterSensor value, Context ctx, Collector<String> out) throws Exception {
                    String key = ctx.getCurrentKey();
                    Integer lastValue = sumMap.getOrDefault(key, 0);
                    sumMap.put(key, lastValue + value.getVc());

                    out.collect(key + "  " + sumMap.get(key));
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
