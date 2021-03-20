package com.atguigu.etl;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class ETLDriver {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        Job job = Job.getInstance(new Configuration());

        job.setJarByClass(ETLDriver.class);

        job.setMapperClass(ETLMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(NullWritable.class);

        //没有Reduce -- 一个MR程序没有Reduce就不会走Shuffle
        job.setNumReduceTasks(0);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);

        FileInputFormat.setInputPaths(job,new Path("D:\\io\\input8"));
        FileOutputFormat.setOutputPath(job,new Path("D:\\io\\output88"));

        job.waitForCompletion(true);

    }
}
