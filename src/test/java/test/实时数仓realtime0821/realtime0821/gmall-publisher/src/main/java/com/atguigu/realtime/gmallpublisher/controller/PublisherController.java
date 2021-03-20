package com.atguigu.realtime.gmallpublisher.controller;

import com.alibaba.fastjson.JSON;
import com.atguigu.realtime.gmallpublisher.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/9 9:30
 */

@RestController
public class PublisherController {

    @Autowired
    public PublisherService service;

    //    http://localhost:8070/realtime-total?date=2021-01-08
    @GetMapping("/realtime-total")
    public String realtimeTotal(String date) throws IOException {
        Long total = service.getDau(date);
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("id", "dau");
        map1.put("name", "新增日活");
        map1.put("value", total);
        result.add(map1);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("id", "new_mid");
        map2.put("name", "新增设备");
        map2.put("value", 233);
        result.add(map2);

        Map<String, Object> map3 = new HashMap<>();
        map3.put("id", "order_amount");
        map3.put("name", "新增交易额");
        map3.put("value", service.getAmountTotal(date));
        result.add(map3);

        return JSON.toJSONString(result);
    }

    // http://localhost:8070/realtime-hour?id=dau&date=2020-10-01
    @GetMapping("/realtime-hour")
    public String realtimeHour(String id, String date) throws IOException, InterruptedException {
        if ("dau".equals(id)) {
            Map<String, Long> today = service.getHourDau(date);
            Map<String, Long> yesterday = service.getHourDau(getYesterday(date));

            Map<String, Map<String, Long>> result = new HashMap<>();
            result.put("today", today);
            result.put("yesterday", yesterday);

            return JSON.toJSONString(result);
        } else if ("order_amount".equals(id)) {
            Map<String, BigDecimal> today = service.getAmountHour(date);
            Map<String, BigDecimal> yesterday = service.getAmountHour(getYesterday(date));

            Map<String, Map<String, BigDecimal>> result = new HashMap<>();
            result.put("today", today);
            result.put("yesterday", yesterday);

            return JSON.toJSONString(result);
        }
        return null;
    }

    private String getYesterday(String date) {
        return LocalDate.parse(date).plusDays(-1).toString();
    }
}
