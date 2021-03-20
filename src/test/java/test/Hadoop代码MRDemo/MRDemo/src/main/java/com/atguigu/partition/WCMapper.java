package com.atguigu.partition;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;


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
