package com.atguigu.outputformat2;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class MyRecordWriter extends RecordWriter<LongWritable, Text> {

    private FSDataOutputStream atguigu;
    private FSDataOutputStream other;
    /*
        在构造器中创建流
     */
    public MyRecordWriter(TaskAttemptContext job){
        try {
            //通过job获取配置文件。
            FileSystem fs = FileSystem.get(job.getConfiguration());
            //Path(Path parent, String child)
            //第一个参数 ：父目录
            //第二个参数 ：文件的名字
            atguigu = fs.create(new Path(FileOutputFormat.getOutputPath(job),
                    "atguigu.log"));
            other = fs.create(new Path(FileOutputFormat.getOutputPath(job),
                    "other.log"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * write方法是写用写出数据的
     * @param key 偏移量
     * @param value 读进来的一行一行的数据
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public void write(LongWritable key, Text value)
            throws IOException, InterruptedException {
        //1.判断该网址是否包含了atguigu
        String address = value.toString() + "\n";
        if (address.contains("atguigu")){
            //2.通过流写出数据 使用流写出到atguigu.log
            /*
                将字符串转成字节数据： getBytes()
                将字节数组转成字符串 : new String(byte[] bytes)
             */
            atguigu.write(address.getBytes());
        }else{
            //2.通过流写出数据 写出到other.log
            other.write(address.getBytes());
        }

    }

    /**
     *  关闭资源
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public void close(TaskAttemptContext context) throws IOException, InterruptedException {
        //3.关闭资源 - 关流
        IOUtils.closeStream(atguigu);
        IOUtils.closeStream(other);
    }
}
