package com.atguigu.realtime.gmallpublisher2.controller;

import com.alibaba.fastjson.JSON;
import com.atguigu.realtime.gmallpublisher2.mapper.TmMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/19 9:29
 */
@RestController
public class PublisherController {
    //    @Autowired
    //    PublisherService service;
    @Autowired
    TmMapper mapper;

    // http://localhost:8070/trademark?startDt=2020-08-01&endDt=2020-09-01&limit=3
    @GetMapping("/trademark")
    public String getTmAmount(String startDt, String endDt, int limit) {
        //        Map<String, BigDecimal> tmAmount = service.getTmAmount(startDt, endDt, limit);
        List<Map<String, Object>> tmAmount = mapper.getTmAmount(startDt, endDt, limit);
        for (Map<String, Object> map : tmAmount) {
            map.put("x", map.get("tm_name"));
            map.remove("tm_name");
            map.put("y", map.get("amount"));
            map.remove("amount");
            map.put("s",1);
        }
        return JSON.toJSONString(tmAmount);
    }
}
