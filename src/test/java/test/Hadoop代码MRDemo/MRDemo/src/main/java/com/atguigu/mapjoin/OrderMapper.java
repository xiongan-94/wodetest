package com.atguigu.mapjoin;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.hash.Hash;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;


public class OrderMapper extends Mapper<LongWritable, Text,OrderBean, NullWritable> {
    //用来存放pd.txt中的内容。key是pid, value是pname
    private HashMap<String,String> pdMap = new HashMap<>();

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        //读取pd.txt
        FileSystem fs = FileSystem.get(context.getConfiguration());
        //获取缓存的路径
        URI[] cacheFiles = context.getCacheFiles();
        FSDataInputStream fis = fs.open(new Path(cacheFiles[0]));
        //读取数据
        //创建字符缓冲流的对象用来一行一行的读取数据
        BufferedReader br = new BufferedReader(new InputStreamReader(fis,"UTF-8"));
        String line = null;
        while((line = br.readLine()) != null){
            //读取了一行内容。切割并存放到Map中
            String[] split = line.split("\t");
            pdMap.put(split[0],split[1]);
        }
        //关闭资源 - 大家写的时候一定要try-catch-finally
        br.close();
        fis.close();
        fs.close();
    }



    /*
                在map方法中读取order.txt的内容

                注意：在执行map方法前就应该将另一个文件已经存到缓存中。
                        这样map每读取一行数据就能获取到对应的pname然后写出去
             */
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        //切出的字段为 ： id pid amount
        String[] split = line.split("\t");
        //封装 OrderBean(String id, String pname, String pid, String amount)
        OrderBean orderBean =
                new OrderBean(split[0], pdMap.get(split[1]), split[1], split[2]);
        //将数据写出去
        context.write(orderBean,NullWritable.get());
    }


}
