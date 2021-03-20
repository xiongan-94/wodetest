package com.atguigu.flink.chapter05.sink;

import com.atguigu.flink.bean.WaterSensor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.http.HttpHost;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/23 11:01
 */
public class Flink03_Sink_Custrom {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);
        Map<String, String> config = new HashMap<>();
        config.put("bulk.flush.max.actions", "1");
        List<HttpHost> esHosts = Arrays.asList(new HttpHost("hadoop162", 9200),
                                               new HttpHost("hadoop163", 9200),
                                               new HttpHost("hadoop164", 9200));

        SingleOutputStreamOperator<WaterSensor> s1 = env
            .socketTextStream("hadoop162", 9999)
            .map(line -> {
                String[] split = line.split(",");
                WaterSensor waterSensor = new WaterSensor(split[0], Long.valueOf(split[1]), Integer.valueOf(split[2]));
                return waterSensor;

            });

        // sink 到mysql
        s1.addSink(new RichSinkFunction<WaterSensor>() {

            private Connection connection;
            private PreparedStatement ps;

            @Override
            public void open(Configuration parameters) throws Exception {
                // 连接到mysql
                connection = DriverManager.getConnection("jdbc:mysql://hadoop162:3306/test", "root", "aaaaaa");
                ps = connection.prepareStatement("replace into sensor values (?, ?, ?)");

            }

            @Override
            public void invoke(WaterSensor value, Context context) throws Exception {
                // 数据写入到mysql
                ps.setString(1, value.getId());
                ps.setLong(2, value.getTs());
                ps.setInt(3, value.getVc());
                ps.execute();
            }

            @Override
            public void close() throws Exception {
                // 关闭到mysql的连接
                ps.close();
                connection.close();
            }

        });


        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
