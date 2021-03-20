package com.atguigu.azkaban;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class HttpUtils {

    /**
     * 发起get请求
     * @param url
     */
    public static void get(String url) throws IOException {

        //1、创建HttpClient
        HttpClient client = new HttpClient();
        //2、创建Method
        GetMethod method = new GetMethod(url);
        //3、发起请求
        int code = client.executeMethod(method);
        //4、判断请求是否成功
        if(code==200){
            //5、返回结果
            String result = method.getResponseBodyAsString();
            System.out.println(result);
        }

    }

    /**
     * 发起Post请求
     * @param url
     * @param content  post请求的参数
     */
    public static void post(String url,String content) throws Exception {

        //创建httpClient
        HttpClient client = new HttpClient();
        //创建method
        PostMethod postMethod = new PostMethod(url);
        //传入参数
        StringRequestEntity entity = new StringRequestEntity(content, "application/json", "utf-8");
        postMethod.setRequestEntity(entity);
        //发起请求
        int code = client.executeMethod(postMethod);
        //判断请求是否成功，如果成功返回结果
        if(code==200){
            System.out.println(postMethod.getResponseBodyAsString());
        }
    }

    public static void main(String[] args) throws Exception {
        //get("https://service.mail.qq.com/cgi-bin/help?subtype=1&&id=28&&no=331");

        String url = "http://api.aiops.com/alert/api/event?app=01fd86ce-428c-4881-bfc0-874f90151f9f&eventId=xxx&eventType=trigger&alarmName=xxx&priority=2";

        String content = "{ \"app\": \"01fd86ce-428c-4881-bfc0-874f90151f9f\", \"eventId\": \"12345\", \"eventType\": \"trigger\", \"alarmName\": \"FAILURE for production/HTTP on machine 192.168.0.253\", \"entityName\": \"host-192.168.0.253\", \"entityId\": \"host-192.168.0.253\", \"priority\": 1, \"alarmContent\": { \"ping time\": \"2500ms\", \"load avg\": 0.75 }, \"details\": { \"details\":\"haha\" }, \"contexts\": [ { \"type\": \"link\", \"text\": \"generatorURL\", \"href\": \"http://www.baidu.com\" }, { \"type\": \"link\", \"href\": \"http://www.sina.com\", \"text\": \"CPU Alerting\" }, { \"type\": \"image\", \"src\": \"http://www.baidu.com/a.png\" }] }";
        post(url,content);


    }
}
