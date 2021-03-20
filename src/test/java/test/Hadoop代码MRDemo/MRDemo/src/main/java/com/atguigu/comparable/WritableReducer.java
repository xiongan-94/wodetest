package com.atguigu.comparable;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class WritableReducer extends Reducer<FlowBean, Text,Text,FlowBean> {
    @Override
    protected void reduce(FlowBean key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        //在reduce中只是key和value调换了下顺序
        for (Text text : values) {
            context.write(text,key);
        }
    }
}
