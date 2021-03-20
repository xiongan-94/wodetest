package com.atguigu.realtime.gmallpublisher.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

public interface PublisherService {
    // http://localhost:8070/realtime-total?date=2021-01-08
    Long getDau(String date) throws IOException;

    // Map("10"->100, "16" -> 110)
    Map<String, Long> getHourDau(String date) throws IOException;

    BigDecimal getAmountTotal(String date);

    // Map("10" -> 100.22, "11"->22.22)
    Map<String, BigDecimal> getAmountHour(String date);
}
/*
┌─create_hour─┬─sum_amout─┐
│ 02          │ 159390.00 │
└─────────────┴───────────┘
│ 03          │ 59390.00  │
└─────────────┴───────────┘
 */