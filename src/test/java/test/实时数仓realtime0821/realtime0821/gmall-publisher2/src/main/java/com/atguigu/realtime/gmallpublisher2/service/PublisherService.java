package com.atguigu.realtime.gmallpublisher2.service;

import java.math.BigDecimal;
import java.util.Map;

public interface PublisherService {
    Map<String, BigDecimal> getTmAmount(String startTime,
                                        String endTime,
                                        int limit);
}
/*
+---------+-----------+
| tm_name | amount    |
+---------+-----------+
| 联想    | 159938.08 |
| 荣耀    |  79852.00 |
| 小米    |  65740.76 |
+---------+-----------+
 */