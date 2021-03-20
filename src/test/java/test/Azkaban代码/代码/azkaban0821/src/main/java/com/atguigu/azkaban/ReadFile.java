package com.atguigu.azkaban;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class ReadFile {

    public static void main(String[] args) throws Exception {

        //1、创建流
        FileReader reader = new FileReader("product.txt");
        BufferedReader br = new BufferedReader(reader);
        //2、读取数据
        String line = null;
        while ( (line=br.readLine())!=null ) {
            System.out.println(line);
        }
        //3、关闭流
        br.close();
        reader.close();
    }
}
