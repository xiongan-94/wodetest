package com.atguigu.comparable2;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WritableDriver {
    public static void main(String[] args) throws Exception {
        Job job = Job.getInstance(new Configuration());
        job.setJarByClass(WritableDriver.class);

        job.setMapperClass(WritableMapper.class);
        job.setReducerClass(WritableReducer.class);


        //设置自定义分区类
        job.setPartitionerClass(MyPartitioner.class);
        //设置reduce的个数
        job.setNumReduceTasks(5);

        job.setMapOutputKeyClass(FlowBean.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FlowBean.class);

        FileInputFormat.setInputPaths(job,new Path("D:\\io\\input5"));
        FileOutputFormat.setOutputPath(job,new Path("D:\\io\\output5"));

        job.waitForCompletion(true);
    }
}
