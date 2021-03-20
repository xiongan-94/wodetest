package com.atguigu.reducejoin;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

public class OrderReducer extends Reducer<OrderBean, NullWritable,OrderBean,NullWritable> {

    /*
            key                                 valule
     null     1     null     小米                 null
     1001   1     1 　       null                 null
     1004   1     4         null                  null

     */
    @Override
    protected void reduce(OrderBean key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
        Iterator<NullWritable> iterator = values.iterator();
        //获取第一个value的值，key指向的对象中的内容也会发生变化。
        iterator.next();
        //获取第一条数据的pname值
        String pname = key.getPname();
        //遍历其它的数据
        while(iterator.hasNext()){
            iterator.next();
            key.setPname(pname);
            //将数据写出去
            context.write(key,NullWritable.get());
        }
    }
}
