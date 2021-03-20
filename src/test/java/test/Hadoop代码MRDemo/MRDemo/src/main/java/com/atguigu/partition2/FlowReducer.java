package com.atguigu.partition2;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class FlowReducer extends Reducer<Text, FlowBean,Text, FlowBean> {

    /*
        key             value
       13568436656	    2481	24681	总流量
       13568436656	    1116	954	    总流量
     */
    @Override
    protected void reduce(Text key, Iterable<FlowBean> values, Context context) throws IOException, InterruptedException {

        long sumUpFlow = 0;
        long sumDownFlow = 0;

        //1.对values进行遍历（value是FlowBean）
        for (FlowBean bean : values) {
            sumUpFlow += bean.getUpFlow();
            sumDownFlow += bean.getDownFlow();
        }

        //2.封装K,V
        FlowBean outValue = new FlowBean(sumUpFlow, sumDownFlow);

        //3.将数据写出去
        context.write(key,outValue);
    }
}
