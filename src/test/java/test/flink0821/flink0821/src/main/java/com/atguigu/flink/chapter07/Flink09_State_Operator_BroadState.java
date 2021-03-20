package com.atguigu.flink.chapter07;

import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/23 11:01
 */
public class Flink09_State_Operator_BroadState {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        env.setParallelism(2);

        DataStreamSource<String> dataStream = env.socketTextStream("hadoop162", 9999);
        // 广播状态可以看成一个Map
        BroadcastStream<String> switchBroadCast = env.socketTextStream("hadoop162", 10000)
            .broadcast(new MapStateDescriptor<>("state", String.class, String.class));
        dataStream.connect(switchBroadCast)
            .process(new BroadcastProcessFunction<String, String, String>() {
                @Override
                public void processElement(String value, ReadOnlyContext ctx, Collector<String> out) throws Exception {
                    ReadOnlyBroadcastState<String, String> state = ctx
                        .getBroadcastState(new MapStateDescriptor<>("state", String.class, String.class));
                    // 0 : 切换到0模式   1:...
                    String mode = state.get("switch");
                    if ("0".equals(mode)) {
                        out.collect("处理模式切换到 0 处理逻辑....");
                    }else if ("1".equals(mode)) {
                        out.collect("处理模式切换到 1 处理逻辑....");
                    }else{
                        out.collect("处理模式切换到 其他 处理逻辑....");

                    }
                }

                @Override
                public void processBroadcastElement(String value, Context ctx, Collector<String> out) throws Exception {
                    // 接受广播流的数据. 存储数据
                    BroadcastState<String, String> state = ctx
                        .getBroadcastState(new MapStateDescriptor<>("state", String.class, String.class));
                    state.put("switch", value);
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

