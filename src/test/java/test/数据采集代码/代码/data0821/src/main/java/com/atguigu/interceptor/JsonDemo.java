package com.atguigu.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class JsonDemo {

    public static void main(String[] args) {

        String js = "{\"name\":\"zhangsan\",\"age\":20";
        JSONObject object = JSON.parseObject(js);
        System.out.println(object.getString("name"));
    }
}
