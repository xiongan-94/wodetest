package com.atguigu.etl;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class ETLMapper extends Mapper<LongWritable,Text, Text, NullWritable> {

    Counter success;
    Counter fail;

    /*
        构造器虽然也只是调用一次但是得不到上下文
     */
    public ETLMapper(){
        //只执行一次
    }


    /*
        该方法会在任务开始的时候只调用一次 ：初始化的操作
     */
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        //获取计数器
        /*
            getCounter(String groupName, String counterName)
            第一个参数 ：组名（随便写）
            第二个参数 ：计算器的名字（随便写）
         */
        success = context.getCounter("ETL", "success");
        fail = context.getCounter("ETL", "fail");
    }

    /*
        该方法会在任务结束的时候调用一次 ：关闭资源
     */
    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {

    }

    /*
                在map方法中对数据进行清洗：
                清洗条件：字段长度大于11可用其它不可用
             */
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {


        String line = value.toString();
        //进行切割
        String[] info = line.split(" ");
        //判断字段的长度
        if (info.length > 11){
            //数据可用 - 写出去
            context.write(value,NullWritable.get());
            //计数器进行累加
            success.increment(1);
        }else{
            fail.increment(1);
        }
    }
}
