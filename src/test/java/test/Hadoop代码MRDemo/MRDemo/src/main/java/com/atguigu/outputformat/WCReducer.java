package com.atguigu.outputformat;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/*
    在ReduceTask执行的代码

    一 泛型：Reducer<KEYIN, VALUEIN, KEYOUT, VALUEOUT>
        两组：
            第一组 ：
                KEYIN ：读取数据的key的类型 （mapper输出的key的类型）
                VALUEIN ：读取数据的value的类型（mapper输出的value的类型）
            第二组 ：
                 KEYOUT ：输出数据的key的类型
                 VALUEOUT ：输出数据的value的类型
 */
public class WCReducer extends Reducer<Text, IntWritable,Text,IntWritable> {
    IntWritable outValue = new IntWritable();
    /*
        在reudce方法中实现需要在ReduceTask任务中实现的功能

        reduce(Text key, Iterable<IntWritable> values, Context context)
        第一个参数 ：读取数据的key的值
        第二个参数 ：读取数据（一组一组的）的所有value
        第三个参数 ： 上下文，在该方法中用来写出数据
     */
    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        //用来统计所有（该组中）的value的和
        int sum = 0;
        //遍历所有的value
        for (IntWritable value : values ) {
            //对所有的value进行累加
            sum += value.get();
        }
        //封装K,V
        outValue.set(sum);
        //将key和value写出
        context.write(key,outValue);
    }
}
