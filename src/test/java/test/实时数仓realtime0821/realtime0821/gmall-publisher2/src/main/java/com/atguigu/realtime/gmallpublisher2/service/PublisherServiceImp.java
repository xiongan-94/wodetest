package com.atguigu.realtime.gmallpublisher2.service;

import com.atguigu.realtime.gmallpublisher2.mapper.TmMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/19 9:24
 */
@Service
public class PublisherServiceImp implements PublisherService {
    @Autowired
    TmMapper tm;

    @Override
    public Map<String, BigDecimal> getTmAmount(String startTime,
                                               String endTime,
                                               int limit) {
        List<Map<String, Object>> tmAmountList = tm.getTmAmount(startTime, endTime, limit);
        Map<String, BigDecimal> result = new HashMap<>();
        for (Map<String, Object> map : tmAmountList) {
            String tm_name = (String) map.get("tm_name");
            BigDecimal amount = (BigDecimal) map.get("amount");
            result.put(tm_name, amount);
        }
        return result;
    }
}
