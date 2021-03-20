package com.atguigu.flink.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/22 11:23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WaterSensor {
    private String id;
    private Long ts;
    private Integer vc;
}
