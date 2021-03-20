package com.atguigu.reducejoin;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class OrderBean implements WritableComparable<OrderBean> {
    private String id;
    private String pname;
    private String pid;
    private String amount;

    public OrderBean() {
    }

    public OrderBean(String id, String pname, String pid, String amount) {
        this.id = id;
        this.pname = pname;
        this.pid = pid;
        this.amount = amount;
    }
    @Override
    public int compareTo(OrderBean o) {
        //先按照pid排序，如果pid相同再按照pname排序
        int ei = this.pid.compareTo(o.pid);
        if (ei == 0){//表示pid相同再排序pname
            return -this.pname.compareTo(o.pname);
        }
        return ei;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(id);
        out.writeUTF(pid);
        out.writeUTF(pname);
        out.writeUTF(amount);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        id = in.readUTF();
        pid = in.readUTF();
        pname = in.readUTF();
        amount = in.readUTF();
    }

    @Override
    public String toString() {
        return id + " " + pid + " " + pname + " " + amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }


}
