package com.atguigu.outputformat2;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/*
    自定义OutputFormat


 */
/*
    FileOutputFormat<K, V> :
        K : 是Reducer写出的Key的类型
        V ： 是Reducer写出的Value的类型

    注意 ：如果没有Mapper和Reducer都没有。 K是偏移量，V是一行一行的内容
 */
public class MyOutputFormat extends FileOutputFormat<LongWritable, Text> {
    /*
        用来获取RecordWriter的对象
     */
    @Override
    public RecordWriter<LongWritable, Text> getRecordWriter(TaskAttemptContext job)
            throws IOException, InterruptedException {
        return new MyRecordWriter(job);
    }
}
