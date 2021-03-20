package com.atguigu.mapjoin;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class OrderDriver {
    public static void main(String[] args) throws IOException, URISyntaxException, ClassNotFoundException, InterruptedException {

        Job job = Job.getInstance(new Configuration());

        job.setJarByClass(OrderDriver.class);

        job.setMapperClass(OrderMapper.class);

        job.setMapOutputKeyClass(OrderBean.class);
        job.setMapOutputValueClass(NullWritable.class);

        job.setOutputKeyClass(OrderBean.class);
        job.setOutputValueClass(NullWritable.class);

        //添加需要缓存的文件路径
        job.addCacheFile(new URI("file:///D:/io/input9/pd.txt"));


        FileInputFormat.setInputPaths(job,
                new Path("D:\\io\\input9\\order.txt"));

        FileOutputFormat.setOutputPath(job,new Path("D:\\io\\output9"));

        job.waitForCompletion(true);
    }
}
