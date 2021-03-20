package com.atguigu.reducejoin;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

/*

    自定义分组的方式：
        1.如果没有自定义分组，那么分组的方式和默认排序的方式相同
        2.自定义分类那么需要自定义一个类并继承WritableComparator
        3.重写compare(WritableComparable a, WritableComparable b)方法
        4.实现需要分组的排序



    注意 ：  WritableComparator是另一种排序的方式。我们主要用在分组中。

 */
public class MyGroup extends WritableComparator {

    public MyGroup(){
        /*
            第一个参数 ：数据对象的类型
            第二个参数 ：是否创建实例
         */
        super(OrderBean.class,true);
    }

    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        //向下转型
        OrderBean o1 = (OrderBean) a;
        OrderBean o2 = (OrderBean) b;
        //按照Pid进行排序，所以就是按照pid进行分组
        return o1.getPid().compareTo(o2.getPid());
    }
}
