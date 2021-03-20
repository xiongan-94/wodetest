package com.atguigu.realtime.gmallpublisher.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface OrderMapper {
    //定义抽象方法, 方法的实现由Mybatis来完成, 我们只需要去调用这个方法
    // 得到的返回值, 就是sql语句执行的结果
    BigDecimal getAmountTotal(String date);
    List<Map<String, Object>> getAmountHour(String date);

}

/*
┌─create_hour─┬─sum_amout─┐
│ 02          │ 159390.00 │
└─────────────┴───────────┘
│ 03          │ 59390.00  │
└─────────────┴───────────┘
 */
