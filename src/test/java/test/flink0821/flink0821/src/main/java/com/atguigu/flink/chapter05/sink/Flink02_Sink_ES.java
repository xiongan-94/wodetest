package com.atguigu.flink.chapter05.sink;

import com.alibaba.fastjson.JSON;
import com.atguigu.flink.bean.WaterSensor;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/23 11:01
 */
public class Flink02_Sink_ES {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        ArrayList<WaterSensor> waterSensors = new ArrayList<>();
        waterSensors.add(new WaterSensor("sensor_1", 1607527992000L, 20));
        waterSensors.add(new WaterSensor("sensor_1", 1607527994000L, 50));
        waterSensors.add(new WaterSensor("sensor_1", 1607527996000L, 50));
        waterSensors.add(new WaterSensor("sensor_2", 1607527993000L, 10));
        waterSensors.add(new WaterSensor("sensor_2", 1607527995000L, 30));
        List<HttpHost> esHosts = Arrays.asList(new HttpHost("hadoop162", 9200),
                                               new HttpHost("hadoop163", 9200),
                                               new HttpHost("hadoop164", 9200));
        env
            .fromCollection(waterSensors)
            .addSink(new ElasticsearchSink.Builder<WaterSensor>(esHosts, new ElasticsearchSinkFunction<WaterSensor>() {
                @Override
                public void process(WaterSensor element, RuntimeContext ctx, RequestIndexer indexer) {
                    // 实现真正向es写数据
                    IndexRequest indexRequest = Requests
                        .indexRequest()
                        .index("sensor")
                        .type("_doc")
                        .id(element.getId())
                        .source(JSON.toJSONString(element), XContentType.JSON);
                    indexer.add(indexRequest);
                }
            }).build());

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
