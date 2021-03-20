package com.atguigu.realtime.gmallpublisher.service;

import com.atguigu.realtime.gmallpublisher.mapper.OrderMapper;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PublisherServiceImp implements PublisherService {

    private static JestClientFactory factory;

    static {
        factory = new JestClientFactory();

        HttpClientConfig config = new HttpClientConfig.Builder("http://hadoop162:9200")
            .connTimeout(10 * 1000)
            .readTimeout(10 * 1000)
            .multiThreaded(true)
            .maxTotalConnection(100)
            .build();
        factory.setHttpClientConfig(config);
    }

    @Override
    public Long getDau(String date) throws IOException {
        JestClient es = factory.getObject();
        // 现有es客户端
        // 查询
        Search search = new Search.Builder(DSLUtil.getTotalDauDSL())
            .addIndex("gmall_dau_info_" + date)
            .addType("_doc")
            .build();
        SearchResult searchResult = es.execute(search);
        // 解析结果
        Long total = searchResult.getTotal();
        es.close();
        // 返回结果
        return total == null ? 0L : total;
    }

    // Map("10"->100, "16" -> 110)
    @Override
    public Map<String, Long> getHourDau(String date) throws IOException {
        JestClient es = factory.getObject();
        Search search = new Search.Builder(DSLUtil.getHourDauDSL())
            .addIndex("gmall_dau_info_" + date)
            .addType("_doc")
            .build();
        SearchResult searchResult = es.execute(search);
        TermsAggregation agg = searchResult
            .getAggregations()
            .getTermsAggregation("group_by_loghour");
        System.out.println(date + " " + agg);
        HashMap<String, Long> result = new HashMap<>();
        if (agg != null) {

            for (TermsAggregation.Entry bucket : agg.getBuckets()) {
                String hour = bucket.getKey();
                Long hourDau = bucket.getCount();
                result.put(hour, hourDau);
            }
        }
        es.close();
        return result;
    }

    @Autowired
    OrderMapper order;

    @Override
    public BigDecimal getAmountTotal(String date) {
        return order.getAmountTotal(date);
    }

    @Override
    public Map<String, BigDecimal> getAmountHour(String date) {
        //        Map("10" -> 100.22, "11"->22.22)
        List<Map<String, Object>> list = order.getAmountHour(date);

        Map<String, BigDecimal> result = new HashMap<>();
        for (Map<String, Object> map : list) {
            String hour = map.get("create_hour").toString();
            BigDecimal amount = (BigDecimal) map.get("sum_amout");
            result.put(hour, amount);
        }

        return result;
    }
}
/*
/*
┌─create_hour─┬─sum_amout─┐
│ 02          │ 159390.00 │
└─────────────┴───────────┘
│ 03          │ 59390.00  │
└─────────────┴───────────┘
 */
