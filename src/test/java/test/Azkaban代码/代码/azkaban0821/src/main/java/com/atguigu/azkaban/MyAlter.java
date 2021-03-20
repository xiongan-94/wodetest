package com.atguigu.azkaban;

import azkaban.alert.Alerter;
import azkaban.executor.ExecutableFlow;
import azkaban.executor.Executor;
import azkaban.executor.ExecutorManagerException;
import azkaban.sla.SlaOption;
import azkaban.utils.Props;

import java.util.List;

public class MyAlter implements Alerter {

    private String url;
    /**
     * 可以从配置文件中获取参数
     * @param props
     */
    public MyAlter(Props props){

        url = props.getString("url");

    }
    /**
     * 成功时发送消息
     * @param executableFlow
     * @throws Exception
     */
    @Override
    public void alertOnSuccess(ExecutableFlow executableFlow) throws Exception {

        String content = "{ \"app\": \"01fd86ce-428c-4881-bfc0-874f90151f9f\", \"eventId\": \"12345\", \"eventType\": \"trigger\", \"alarmName\": \"FAILURE for production/HTTP on machine 192.168.0.253\", \"entityName\": \"host-192.168.0.253\", \"entityId\": \"host-192.168.0.253\", \"priority\": 1, \"alarmContent\": { \"ping time\": \"2500ms\", \"load avg\": 0.75 }, \"details\": { \"details\":\"haha\" }, \"contexts\": [ { \"type\": \"link\", \"text\": \"generatorURL\", \"href\": \"http://www.baidu.com\" }, { \"type\": \"link\", \"href\": \"http://www.sina.com\", \"text\": \"CPU Alerting\" }, { \"type\": \"image\", \"src\": \"http://www.baidu.com/a.png\" }] }";
        HttpUtils.post(url,content);
    }

    /**
     * 失败时发送消息
     * @param executableFlow
     * @param strings
     * @throws Exception
     */
    @Override
    public void alertOnError(ExecutableFlow executableFlow, String... strings) throws Exception {

        String content = "{ \"app\": \"01fd86ce-428c-4881-bfc0-874f90151f9f\", \"eventId\": \"12345\", \"eventType\": \"trigger\", \"alarmName\": \"FAILURE for production/HTTP on machine 192.168.0.253\", \"entityName\": \"host-192.168.0.253\", \"entityId\": \"host-192.168.0.253\", \"priority\": 1, \"alarmContent\": { \"ping time\": \"2500ms\", \"load avg\": 0.75 }, \"details\": { \"details\":\"haha\" }, \"contexts\": [ { \"type\": \"link\", \"text\": \"generatorURL\", \"href\": \"http://www.baidu.com\" }, { \"type\": \"link\", \"href\": \"http://www.sina.com\", \"text\": \"CPU Alerting\" }, { \"type\": \"image\", \"src\": \"http://www.baidu.com/a.png\" }] }";
        HttpUtils.post(url,content);
    }

    /**
     * 第一次失败发送消息
     * @param executableFlow
     * @throws Exception
     */
    @Override
    public void alertOnFirstError(ExecutableFlow executableFlow) throws Exception {

    }

    @Override
    public void alertOnSla(SlaOption slaOption, String s) throws Exception {

    }

    @Override
    public void alertOnFailedUpdate(Executor executor, List<ExecutableFlow> list, ExecutorManagerException e) {

    }
}
