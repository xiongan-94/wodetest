package com.atguigu.partition2;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class FlowDriver {
    public static void main(String[] args) throws Exception {

        Job job = Job.getInstance(new Configuration());
        job.setJarByClass(FlowDriver.class);

        //设置使用自定义分区
        job.setPartitionerClass(MyPartitioner.class);
        //设置reduce的个数
        /*
            注意：
                1.分区的数量 = reduce的数量
                2.分区的数量 < reduce的数量 （因为一个reduce输出一个文件）
                3.分区的数量 > reduce的数量会报错。
         */
        job.setNumReduceTasks(5);


        job.setMapperClass(FlowMapper.class);
        job.setReducerClass(FlowReducer.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(FlowBean.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FlowBean.class);
        FileInputFormat.setInputPaths(job,new Path("D:\\io\\input2"));
        FileOutputFormat.setOutputPath(job,new Path("D:\\io\\output22"));
        job.waitForCompletion(true);
    }
}
