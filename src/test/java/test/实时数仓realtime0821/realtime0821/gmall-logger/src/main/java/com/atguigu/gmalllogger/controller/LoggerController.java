package com.atguigu.gmalllogger.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/4 9:30
 */
/*@Controller
@ResponseBody*/
@RestController  // 等价于@Controller+@ResponseBody
@Slf4j
public class LoggerController {
    //    @GetMapping("/applog")
    @PostMapping("/applog")
    public String doLog(@RequestBody String strLog) {
        // 1. 把数据落盘(给离线需求使用)
        saveToDisk(strLog);
        // 2. 数据写入到Kafka集群
        sendToKafka(strLog);
        return "ok";
    }

    @Autowired
    KafkaTemplate<String, String> kafka;

    // 写数到Kafka集群
    private void sendToKafka(String strLog) {
        JSONObject obj = JSON.parseObject(strLog);
        // 不同的日志写入到不同的Topic中
        if (obj.containsKey("start") && obj.getString("start").length() > 10) {
            kafka.send("gmall_start_topic", strLog);
        } else if (!obj.containsKey("start")) {
            kafka.send("gmall_event_topic", strLog);
        }
    }

    //把日志写入到磁盘
    private void saveToDisk(String strLog) {
        log.info(strLog);
    }
}
