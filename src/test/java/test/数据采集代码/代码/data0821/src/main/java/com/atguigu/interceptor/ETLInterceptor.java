package com.atguigu.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;

import java.util.Iterator;
import java.util.List;

public class ETLInterceptor implements Interceptor {

    /**
     * 用于初始化
     */
    @Override
    public void initialize() {

    }

    /**
     * 对单个数据进行处理
     * @param event
     * @return
     */
    @Override
    public Event intercept(Event event) {

        //1、取出数据
        byte[] body = event.getBody();
        String json = new String(body);
        //2、判断是否为json数据，如果是json数据保留，如果不是json数据就返回null
        try{
            JSON.parseObject(json);
            return event;
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 对一个批次的数据进行处理
     * @param list
     * @return
     */
    @Override
    public List<Event> intercept(List<Event> list) {

        Iterator<Event> it = list.iterator();

        while (it.hasNext()){
            Event event = it.next();
            if(intercept(event)==null) it.remove();
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
         * 用于flume创建自定义拦截器对象
         * @return
         */
        @Override
        public Interceptor build() {
            return new ETLInterceptor();
        }

        @Override
        public void configure(Context context) {

        }
    }

}
