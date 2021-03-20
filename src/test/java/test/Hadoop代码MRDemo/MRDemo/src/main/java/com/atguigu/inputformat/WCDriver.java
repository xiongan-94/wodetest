package com.atguigu.inputformat;

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

        //虚拟存储切片最大值设置
        CombineTextInputFormat.setMaxInputSplitSize(job, 4194304*10);// 4m
        //设置InputFormat（CombineTextInputFormat）
        job.setInputFormatClass(CombineTextInputFormat.class);



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
        //6.设置输入和输出的路径
        FileInputFormat.setInputPaths(job,new Path("D:\\io\\input3"));
        FileOutputFormat.setOutputPath(job,new Path("D:\\io\\output33"));
        //7.执行任务
        /*
            waitForCompletion(boolean verbose)
            verbose : 如果为true则打印job执行的进度
            返回值 ： 如果返回值为true则说明job执行成功
         */
        boolean boo = job.waitForCompletion(true);
        //退出虚拟机 ：如果为0表示正常退出，如果为1非正常退出
        System.exit(boo ? 0 : 1);
    }
}
