package com.atguigu.flink.chapter05.sink;

import com.alibaba.fastjson.JSON;
import com.atguigu.flink.bean.WaterSensor;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;

import java.util.ArrayList;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/23 11:01
 */
public class Flink01_Sink_Redis {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        ArrayList<WaterSensor> waterSensors = new ArrayList<>();
        waterSensors.add(new WaterSensor("sensor_1", 1607527992000L, 20));
        waterSensors.add(new WaterSensor("sensor_1", 1607527994000L, 50));
        waterSensors.add(new WaterSensor("sensor_1", 1607527996000L, 50));
        waterSensors.add(new WaterSensor("sensor_2", 1607527993000L, 10));
        waterSensors.add(new WaterSensor("sensor_2", 1607527995000L, 30));

        FlinkJedisPoolConfig conf = new FlinkJedisPoolConfig.Builder()
            .setHost("hadoop162")
            .setPort(6379)
            .setMaxTotal(100)
            .setMaxIdle(10)
            .setTimeout(10 * 1000)
            .build();
        // 把数据写入到Hash
        /*
        key             value
        "sensor"        "sensor_1": {...}
         */
        env
            .fromCollection(waterSensors)
            .keyBy(WaterSensor::getId)
            .sum("vc")
            .addSink(new RedisSink<>(conf, new RedisMapper<WaterSensor>() {
                @Override
                public RedisCommandDescription getCommandDescription() {
                    // 获取命令描述符
                    return new RedisCommandDescription(RedisCommand.HSET, "sensor");
                }

                @Override
                public String getKeyFromData(WaterSensor data) {
                    // 返回hash的内层的key
                    return data.getId();
                }

                @Override
                public String getValueFromData(WaterSensor data) {
                    return JSON.toJSONString(data);
                }
            }));

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
