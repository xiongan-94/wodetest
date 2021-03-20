package com.atguigu.comparable;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class WritableMapper extends Mapper<LongWritable, Text,FlowBean,Text> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        //进行切割
        String[] flow = line.split(" ");
        //封装K,V
        FlowBean outKey = new FlowBean(
                Long.parseLong(flow[1]),
                Long.parseLong(flow[2]),
                Long.parseLong(flow[3]));
        Text outValue = new Text();
        outValue.set(flow[0]);
        //写数据
        context.write(outKey,outValue);
    }
}
