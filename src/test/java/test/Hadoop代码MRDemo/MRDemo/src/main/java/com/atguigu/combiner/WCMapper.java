package com.atguigu.combiner;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/*
    是在MapTask阶段执行的代码

    一 四个泛型：Mapper<KEYIN, VALUEIN, KEYOUT, VALUEOUT>
        两对：
            第一对 ：
                KEYIN : 读取的行数的偏移量
                VALUEIN ： 读取的数据（一行一行的数据）

            第二对：
                KEYOUT ： 输出的key的类型（单词）
                VALUEOUT ： 输出的value的类型（单词的数量）
 */
public class WCMapper extends Mapper<LongWritable, Text,Text, IntWritable> {
    Text outKey = new Text();
    IntWritable outValue = new IntWritable();
    /*
        在map方法中实现具体的代码

        map(LongWritable key, Text value, Context context)
        第一个参数 ：偏移量
        第二个参数 ：读取进来的数据（一行一行的数据）
        第三个参数 ： 上下文（在当前类中使用context向往写数据）
     */
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //将Text转换成字符串
        String line = value.toString();
        //进行单词的切割 - 参考源数据
        String[] words = line.split(" ");
        //遍历所有的单词并进行封装（K,V）
        for (String word : words) {
            //封装key
            outKey.set(word);
            //封装value
            outValue.set(1);
            //将key和value写出
            context.write(outKey,outValue);
        }
    }
}
