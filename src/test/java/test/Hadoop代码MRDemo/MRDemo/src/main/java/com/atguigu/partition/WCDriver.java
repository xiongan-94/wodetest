package com.atguigu.partition;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.CombineTextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


/*
    主程序：程序的入口
 */
public class WCDriver {
    public static void main(String[] args) throws Exception {
        //1.创建一个Job对象
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);
        //2.设置Jar加载的路径
        job.setJarByClass(WCDriver.class);
        //3.设置Mapper和Reducer
        job.setMapperClass(WCMapper.class);
        job.setReducerClass(WCReducer.class);
        //4.设置Mapper的输出的k,v的类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        //5.设置最终（Reducer）输出的k,v的类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        //设置reduce的个数 - 默认为1
        //job.setNumReduceTasks(2);


        FileInputFormat.setInputPaths(job,new Path("D:\\io\\input"));
        FileOutputFormat.setOutputPath(job,new Path("D:\\io\\output333"));
        boolean boo = job.waitForCompletion(true);
        System.exit(boo ? 0 : 1);
    }
}
