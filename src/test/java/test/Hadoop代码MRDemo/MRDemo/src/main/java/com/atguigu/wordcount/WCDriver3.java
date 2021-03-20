package com.atguigu.wordcount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


/*

    在windows上向集群提交job

    一 设置Configuration中配置的内容
        //设置在集群运行的相关参数-设置HDFS,NAMENODE的地址
        conf.set("fs.defaultFS", "hdfs://hadoop102:9820");
        //指定MR运行在Yarn上
        conf.set("mapreduce.framework.name","yarn");
        //指定MR可以在远程集群运行
        conf.set("mapreduce.app-submission.cross-platform","true");
        //指定yarn resourcemanager的位置
        conf.set("yarn.resourcemanager.hostname", "hadoop103");

    二  打包

    三  将job.setJarByClass(WCDriver3.class);给注掉

    四  设置jar的路径
        job.setJar（String jar）

    五 需要在IDEA中进行配置：
        //使用哪个用户操作集群
        VM OPTIONS ： -DHADOOP_USER_NAME=atguigu
        //设置数据的输入和输出路径
        PROGRAM ARGUMENTS ： hdfs://hadoop102:9820/input  hdfs://hadoop102:9820/output2

 */
public class WCDriver3 {
    public static void main(String[] args) throws Exception {
        //校验 - 是否传了两个路径进来。

        //1.创建一个Job对象
        Configuration conf = new Configuration();


        //设置在集群运行的相关参数-设置HDFS,NAMENODE的地址
        conf.set("fs.defaultFS", "hdfs://hadoop102:9820");
        //指定MR运行在Yarn上
        conf.set("mapreduce.framework.name","yarn");
        //指定MR可以在远程集群运行
        conf.set("mapreduce.app-submission.cross-platform","true");
        //指定yarn resourcemanager的位置
        conf.set("yarn.resourcemanager.hostname", "hadoop103");

        conf.set("mapred.job.queue.name", "hive");

        Job job = Job.getInstance(conf);
        //2.设置Jar加载的路径
        //job.setJarByClass(WCDriver3.class);

        //setJar(String jar)
        //参数 ：jar包的路径
        job.setJar("D:\\class_video\\0821\\06_hadoop\\3.代码\\MRDemo\\target\\MRDemo-1.0-SNAPSHOT.jar");


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
        FileInputFormat.setInputPaths(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job,new Path(args[1]));
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
