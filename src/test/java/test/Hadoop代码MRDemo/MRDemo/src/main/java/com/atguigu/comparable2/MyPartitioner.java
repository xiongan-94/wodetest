package com.atguigu.comparable2;


import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

/*
    自定义分区：
    手机号136、137、138、139开头都分别放到一个独立的4个文件中，其他开头的放到一个文件中。


    Partitioner<KEY, VALUE>:
        KEY : map输出的key
        VALUE: map输出的value

 */
public class MyPartitioner extends Partitioner<FlowBean, Text> {

    @Override
    public int getPartition(FlowBean flowBean, Text text, int numPartitions) {
        String phone = text.toString();

        if (phone.startsWith("136")){
            return 0;
        }else if(phone.startsWith("137")){
            return 1;
        }else if(phone.startsWith("138")){
            return 2;
        }else if(phone.startsWith("139")){
            return 3;
        }else{
            return 4;
        }
    }
}
