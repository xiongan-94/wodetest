package com.atguigu.outputformat2;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class OutputFormatDriver {
    public static void main(String[] args) throws Exception {

        Job job = Job.getInstance(new Configuration());

        job.setJarByClass(OutputFormatDriver.class);

        //设置OutputFormat
        job.setOutputFormatClass(MyOutputFormat.class);

        //设置输入和输出路径
        FileInputFormat.setInputPaths(job,new Path("D:\\io\\input7"));
        FileOutputFormat.setOutputPath(job,new Path("D:\\io\\output7"));

        //执行job
        job.waitForCompletion(true);

    }
}
