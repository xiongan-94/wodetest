package com.atguigu.flink.chapter07;

import com.atguigu.flink.bean.WaterSensor;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.contrib.streaming.state.RocksDBStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;

import java.util.Properties;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/27 9:08
 */
public class Flink15_State_Kafka_Flink_Kafka {
    public static void main(String[] args) throws Exception {
        System.setProperty("HADOOP_USER_NAME", "atguigu");
        StreamExecutionEnvironment env = StreamExecutionEnvironment
            .getExecutionEnvironment();

        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "hadoop162:9092,hadoop163:9092,hadoop164:9092");
        props.setProperty("group.id", "Flink01_Source_Kafka");
        props.setProperty("auto.offset.reset", "latest");

        // 状态后端
        env.setStateBackend(new RocksDBStateBackend("hdfs://hadoop162:8020/rock"));
        env.enableCheckpointing(2000);

        // 高级选项:
        // 设置模式为精确一次 (这是默认值)
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);

        // 确认 checkpoints 之间的时间会暂停 500 ms
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(500);

        // Checkpoint 必须在一分钟内完成，否则就会被抛弃
        env.getCheckpointConfig().setCheckpointTimeout(60000);

        // 同一时间只允许一个 checkpoint 进行
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);

        // 开启在 job 中止后仍然保留的 externalized checkpoints
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

        env
            .addSource(new FlinkKafkaConsumer<String>("sensor", new SimpleStringSchema(), props))
            .map(value -> {
                System.out.println(value);
                String[] datas = value.split(",");
                return new WaterSensor(datas[0], Long.valueOf(datas[1]), Integer.valueOf(datas[2]));

            })
            .keyBy(WaterSensor::getId)
            .process(new KeyedProcessFunction<String, WaterSensor, String>() {
                private ValueState<Integer> state;

                @Override
                public void open(Configuration parameters) throws Exception {
                    state = getRuntimeContext().getState(new ValueStateDescriptor<Integer>("state", Integer.class));

                }

                @Override
                public void processElement(WaterSensor value, Context ctx, Collector<String> out) throws Exception {
                    Integer lastVc = state.value() == null ? 0 : state.value();
                    if (Math.abs(value.getVc() - lastVc) >= 10) {
                        out.collect(value.getId() + " 红色警报!!!");
                    }
                    state.update(value.getVc());
                }
            })
            .addSink(new FlinkKafkaProducer<String>("hadoop162:9092", "alert", new SimpleStringSchema()));

        env.execute();
    }
}
