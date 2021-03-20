package com.atguigu.azkaban;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class JsonUtil {

    public static void main(String[] args) {

        //1、对象转json字符串
        Student student = new Student("lisi", 20);

        String json = JSON.toJSONString(student);

        System.out.println(json);
        //2、json字符串转对象

        String sjson = "{\"age\":100,\"name\":\"wangwu\"}";

        Student sty = JSON.parseObject(sjson, Student.class);
        System.out.println(sty.getName());
        System.out.println(sty.getAge());

        //获取json串中属性值
        JSONObject obj = JSON.parseObject(sjson);
        String name = obj.getString("name");
        Integer age = obj.getInteger("age");
        System.out.println(name+"--"+age);

    }
}
