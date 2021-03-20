package com.atguigu.flow;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class FlowDriver {
    public static void main(String[] args) throws Exception {

        //1.创建Job对象
        Job job = Job.getInstance(new Configuration());
        //设置Jar加载的路径(如果是本地模式可以不设置)
        job.setJarByClass(FlowDriver.class);

        //2.设置Mapper和Reducer的类
        job.setMapperClass(FlowMapper.class);
        job.setReducerClass(FlowReducer.class);
        //3.设置Mapper输出的类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(FlowBean.class);
        //4.设置最终（Reducer）输出的类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FlowBean.class);
        //5.设置输入输出的路径
        FileInputFormat.setInputPaths(job,new Path("D:\\io\\input2"));
        FileOutputFormat.setOutputPath(job,new Path("D:\\io\\output2"));
        //6.执行job
        job.waitForCompletion(true);
    }
}
