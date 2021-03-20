package com.atguigu.hive;

import org.apache.hadoop.hive.ql.exec.UDF;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by VULCAN on 2020/12/14
 *      自定义UDF
 */
public class MyUDF extends UDF {

    public static void main(String[] args) {

        String str1="a|b|c";
        String str2=null;
        String str3="b|d|e";

        System.out.println(new MyUDF().evaluate(str2, str3));

       /* String[] words = str.split("\\|");

        for (String s :words ) {
            System.out.println(s);
        }

        System.out.println(words.length);*/

    }

    /*
           函数是在full join之后调用的， oldBrand 和  newBrand 都有可能为NULL

           oldBrand格式：brand1 | brand2　
           newBrand格式：brand1 | brand2
     */
    public String evaluate(String oldBrand,String newBrand){

        if (oldBrand == null){
            return newBrand;
        }

        if (newBrand == null){
            return oldBrand;
        }

        //讲oldBrand 和　newBrand　进行结合去重
        String[] words1 = oldBrand.split("\\|");
        String[] words2 = newBrand.split("\\|");

        // set集合可以去重
        HashSet<String> result = new HashSet<>(Arrays.asList(words1));

        result.addAll(Arrays.asList(words2));

        String str="";
        //讲去重后的结果转为str
        for (String s : result) {
            str += s + "|";
        }

        return str.substring(0,str.length()-1);

    }
}
