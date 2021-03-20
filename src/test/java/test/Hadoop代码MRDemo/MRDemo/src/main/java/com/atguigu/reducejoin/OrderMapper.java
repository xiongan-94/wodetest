package com.atguigu.reducejoin;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;

public class OrderMapper extends Mapper<LongWritable, Text,OrderBean, NullWritable> {

    private String fileName;
    /*
        每个任务开始执行的时候会调用一次该方法
     */
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        //获取切片信息
        FileSplit fs = (FileSplit) context.getInputSplit();
        //根据切片信息获取到该文件的名字
        fileName = fs.getPath().getName();
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //问题 ：无法判断到底是哪个文件中的数据
        String line = value.toString();
        String[] split = line.split("\t");
        OrderBean orderBean = new OrderBean();
        //判断读取到的数据是order.txt还是pd.txt
        //注意 ：属性值不能为null
        if("order.txt".equals(fileName)){//order.txt
            orderBean.setId(split[0]);
            orderBean.setPid(split[1]);
            orderBean.setAmount(split[2]);
            orderBean.setPname("");
        }else if("pd.txt".equals(fileName)){//pd.txt
            orderBean.setPid(split[0]);
            orderBean.setPname(split[1]);
            orderBean.setId("");
            orderBean.setAmount("");
        }
        //写出数据
        context.write(orderBean,NullWritable.get());
    }
}














