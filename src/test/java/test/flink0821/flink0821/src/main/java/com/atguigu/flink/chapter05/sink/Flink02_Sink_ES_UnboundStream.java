package com.atguigu.flink.chapter05.sink;

import com.alibaba.fastjson.JSON;
import com.atguigu.flink.bean.WaterSensor;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/23 11:01
 */
public class Flink02_Sink_ES_UnboundStream {
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

        ElasticsearchSink.Builder<WaterSensor> builder = new ElasticsearchSink.Builder<>(esHosts, new ElasticsearchSinkFunction<WaterSensor>() {
            @Override
            public void process(WaterSensor element, RuntimeContext ctx, RequestIndexer indexer) {
                IndexRequest indexRequest = Requests
                    .indexRequest()
                    .index("sensor")
                    .type("_doc")
                    .id(element.getId())
                    .source(JSON.toJSONString(element), XContentType.JSON);
                indexer.add(indexRequest);
            }
        });
        // 来一条数据, 向es sink一次
        //builder.setBulkFlushMaxActions(1);
        builder.setBulkFlushInterval(10000);

        s1.addSink(builder.build());

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
