package com.atguigu.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;

import java.util.List;
import java.util.Map;

public class MyTimeStampInterceptor implements Interceptor {

    /**
     * 初始化
     */
    @Override
    public void initialize() {

    }

    /**
     * 单个数据的处理
     * @param event
     * @return
     */
    @Override
    public Event intercept(Event event) {
        //1、取出数据
        String json = new String(event.getBody());
        //2、取出时间戳
        JSONObject obj = JSON.parseObject(json);
        Long ts = obj.getLong("ts");
        //3、将时间戳写入header
        Map<String, String> headers = event.getHeaders();
        headers.put("timestamp",ts+"");
        //4、数据返回
        return event;
    }

    /**
     * 批次数据的处理
     * @param list
     * @return
     */
    @Override
    public List<Event> intercept(List<Event> list) {

        for(Event event: list){
            intercept(event);
        }
        return list;
    }

    /**
     * 关闭
     */
    @Override
    public void close() {

    }

    public static class Builder implements org.apache.flume.interceptor.Interceptor.Builder{
        /**
         * 用于Flume创建拦截器对象
         * @return
         */
        @Override
        public Interceptor build() {
            return new MyTimeStampInterceptor();
        }

        @Override
        public void configure(Context context) {

        }
    }
}
