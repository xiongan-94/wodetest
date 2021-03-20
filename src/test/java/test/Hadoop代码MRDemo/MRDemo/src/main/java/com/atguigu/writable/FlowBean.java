package com.atguigu.writable;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/*
    Hadoop的序列化框架

    一  自定义类并实现Writable接口

    二 重写 write和readFields方法

    三  write方法是用来序列化的.readFileds方法是用来反序列化的

    四 注意 ： 读数据时的顺序必须和写数据时的顺序保持一致

 */
public class FlowBean implements Writable {

    //属性的类型如果是基本数据类型可以正常使用。因为基本数据类型不用考虑是否可以序列化的问题。
    //如果属性的类型是包装类的类型那么就必须使用Hadoop提供的对应的那些类（LongWritable,IntWritable）
    private long upFlow;
    private long downFlow;
    private long sumFlow;

    public FlowBean(){

    }

    public FlowBean(long upFlow, long downFlow, long sumFlow) {
        this.upFlow = upFlow;
        this.downFlow = downFlow;
        this.sumFlow = sumFlow;
    }

    /*
        序列化时调用的方法
     */
    public void write(DataOutput out) throws IOException {
        out.writeLong(upFlow);
        out.writeLong(downFlow);
        out.writeLong(sumFlow);
    }

    /*
        反序列化时调用的方法
        注意 ： 读数据时的顺序必须和写数据时的顺序保持一致
     */
    public void readFields(DataInput in) throws IOException {
        upFlow = in.readLong();
        downFlow = in.readLong();
        sumFlow = in.readLong();
    }

    public long getUpFlow() {
        return upFlow;
    }

    public void setUpFlow(long upFlow) {
        this.upFlow = upFlow;
    }

    public long getDownFlow() {
        return downFlow;
    }

    public void setDownFlow(long downFlow) {
        this.downFlow = downFlow;
    }

    public long getSumFlow() {
        return sumFlow;
    }

    public void setSumFlow(long sumFlow) {
        this.sumFlow = sumFlow;
    }


}
