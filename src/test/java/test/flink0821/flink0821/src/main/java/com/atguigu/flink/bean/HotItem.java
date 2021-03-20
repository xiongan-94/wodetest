package com.atguigu.flink.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotItem {
    private Long itemId;  // 产品id
    private Long count;  // pv的次数
    private Long windowEndTime;  // 窗口结束时间
}
