package com.atguigu.partition2;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

/*
    自定义分区：
    手机号136、137、138、139开头都分别放到一个独立的4个文件中，其他开头的放到一个文件中。


    Partitioner<KEY, VALUE>:
        KEY : map输出的key
        VALUE: map输出的value

 */
public class MyPartitioner extends Partitioner<Text,FlowBean> {
    /*
        第一个参数 ：map输出的key
		第二个参数 ：map输出的value
		第三个参数 ：设置的reduce的个数

		返回值 ： 分区号 -- 如果有多个分区那么从0开始依次递增。 0，1，2，3...........
     */
    public int getPartition(Text text, FlowBean flowBean, int numPartitions) {
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
