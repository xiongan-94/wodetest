package com.atguigu.flow;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class FlowMapper extends Mapper<LongWritable,Text, Text,FlowBean> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //1.将Text转成String
        String line = value.toString();
        //2.将数据进行切割
        String[] info = line.split("\t");
        //3.封装K,V
        Text outKey = new Text();
        outKey.set(info[1]);
        FlowBean outValue = new FlowBean(Long.parseLong(info[info.length - 3]),
                Long.parseLong(info[info.length - 2]));
        //4.将K,V写出
        context.write(outKey,outValue);
    }
}
