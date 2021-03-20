package com.atguigu.flink.chapter05.source;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/22 11:32
 */
public class Flink02_Source_File {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        // 1. 相对路径  可以是目录, 也可以是文件. 如果传的是目录, 则只会读里面的文件, 子目录会忽略
//        DataStreamSource<String> sourceStream = env.readTextFile("input");
        // 2. 绝对路径
//        DataStreamSource<String> sourceStream = env.readTextFile("c:/jps.txt");
        // 3. hdfs的文件
//        DataStreamSource<String> sourceStream = env.readTextFile("hdfs://hadoop162:8020/README.txt");
        // 注意: flink不读取core-site.xml 找到默认文件系统
        DataStreamSource<String> sourceStream = env.readTextFile("/README.txt");

        sourceStream.print();

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
