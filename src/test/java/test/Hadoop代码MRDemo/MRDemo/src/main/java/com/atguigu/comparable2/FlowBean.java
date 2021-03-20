package com.atguigu.comparable2;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/*
    1.如果自定义的类要作为Key进行排序那么必须实现WritableComparable接口
    2.WritableComparable接口继承了 Writable, Comparable<T>
    3.重写compareTo方法（在该方法指明具体按照哪个属性进行排序）。
 */

public class FlowBean implements WritableComparable<FlowBean> {

    private long upFlow;
    private long downFlow;
    private long sumFlow;

    public FlowBean(){

    }

    public FlowBean(long upFlow, long downFlow,long sumFlow) {
        this.upFlow = upFlow;
        this.downFlow = downFlow;
        this.sumFlow = sumFlow;
    }

    /*
        具体排序功能的实现
     */
    public int compareTo(FlowBean o) {
        return Long.compare(this.sumFlow,o.sumFlow);
    }

    public void write(DataOutput out) throws IOException {
        out.writeLong(upFlow);
        out.writeLong(downFlow);
        out.writeLong(sumFlow);
    }


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

    @Override
    public String toString() {
        return upFlow + " " + downFlow + " " + sumFlow;
    }


}
