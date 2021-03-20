package com.atguigu.flink.chapter05.transform;

import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/22 14:57
 */
public class Flink05_Transform_Connect {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);
        DataStreamSource<Integer> s1 = env
            .fromElements(1, 2, 3, 4);

        DataStreamSource<String> s2 = env
            .fromElements("10", "20", "30", "40", "50");

        ConnectedStreams<Integer, String> connectStream = s1.connect(s2);

       /* DataStream<Integer> firstInput = connectStream.getFirstInput();
        DataStream<String> secondInput = connectStream.getSecondInput();

        firstInput.print("first");
        secondInput.print("second");*/

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
