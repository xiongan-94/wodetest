package com.atguigu.outputformat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;


/*
    主程序：程序的入口
 */
public class WCDriver {
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);
        job.setJarByClass(WCDriver.class);
        job.setMapperClass(WCMapper.class);

        //设置OutputFormat为SequenceFileOutputFormat
        job.setOutputFormatClass(SequenceFileOutputFormat.class);

        job.setReducerClass(WCReducer.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.setInputPaths(job,new Path("D:\\io\\input"));
        FileOutputFormat.setOutputPath(job,new Path("D:\\io\\output"));
        boolean boo = job.waitForCompletion(true);
        System.exit(boo ? 0 : 1);
    }
}
