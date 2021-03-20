package com.atguigu.realtime.gmallpublisher2.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TmMapper {
    List<Map<String, Object>> getTmAmount(@Param("startTime") String startTime,
                                          @Param("endTime") String endTime,
                                          @Param("limit") int limit);
}
/*
+---------+-----------+
| tm_name | amount    |
+---------+-----------+
| 联想    | 159938.08 |
| 荣耀    |  79852.00 |
| 小米    |  65740.76 |
+---------+-----------+


select
    tm_name,
    sum(amount) amount
from tm_amount
where stat_time>='2021-01-19' and stat_time<'2021-01-20'
group by tm_name
order by amount desc
limit 3
 */