package com.atguigu.flink.chapter05.source;

import com.atguigu.flink.bean.WaterSensor;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/22 11:32
 */
public class Flink04_Source_Custorm {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<WaterSensor> stream = env.addSource(new MySocketSource("hadoop162", 9999));
        stream.print();

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 自定义source, 从socket读取数据, 并封装成pojo
    public static class MySocketSource implements SourceFunction<WaterSensor> {
        private String host;
        private int port;
        private boolean isCancel = false;
        private Socket socket;
        private BufferedReader reader;

        public MySocketSource(String host, int port) {
            this.host = host;
            this.port = port;
        }

        @Override
        public void run(SourceContext<WaterSensor> ctx) throws Exception {
            socket = new Socket(host, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

            String line = reader.readLine();
            while (line != null && !isCancel) {
                String[] split = line.split(",");
                WaterSensor waterSensor = new WaterSensor(split[0], Long.valueOf(split[1]), Integer.valueOf(split[2]));
                ctx.collect(waterSensor);
                line = reader.readLine();
            }
        }

        @Override
        public void cancel() {
            isCancel = true;
            try {
                reader.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
