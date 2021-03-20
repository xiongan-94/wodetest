package com.atguigu.flink.chapter05.transform;

import com.atguigu.flink.bean.WaterSensor;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.ArrayList;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/22 14:57
 */
public class Flink07_Transform_Reduce {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        ArrayList<WaterSensor> waterSensors = new ArrayList<>();
        waterSensors.add(new WaterSensor("sensor_1", 1L, 20));
        waterSensors.add(new WaterSensor("sensor_2", 4L, 10));
//        waterSensors.add(new WaterSensor("sensor_1", 2L, 50));
//        waterSensors.add(new WaterSensor("sensor_1", 3L, 50));
//        waterSensors.add(new WaterSensor("sensor_2", 5L, 30));

        env
            .fromCollection(waterSensors)
            .keyBy(WaterSensor::getId)
            .reduce(new ReduceFunction<WaterSensor>() {
                @Override
                public WaterSensor reduce(WaterSensor v1, WaterSensor v2) throws Exception {
                    System.out.println("reduce...");
                    return new WaterSensor(v1.getId(), v2.getTs(),v1.getVc() + v2.getVc() );
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
