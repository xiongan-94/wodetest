package com.atguigu;

import java.util.Arrays;
import java.util.List;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/19 14:54
 */
public class Demo3 {
    public static void main(String[] args) {
        /*int[] arr = {10, 1, 3, 20, 15};
        Arrays.stream(arr)*/

        List<Integer> list = Arrays.asList(1, 20, 13, 40, 15);

        /*List<Integer> list1 = list.stream()
            .map(x -> x * x)
            .filter(x -> x >= 20)
            .collect(Collectors.toList());
        System.out.println(list1);*/

       /* List<Integer> r = list.stream().map(x -> {
            System.out.println("map....");
            return x * x;
        }).collect(Collectors.toList());
        System.out.println(r);*/
       /* Integer r = list.stream()
            .reduce(Integer::sum)
            .get();
        System.out.println(r);*/

//        list.stream().forEach(System.out::println);
        list.forEach(System.out::println);

        //list.forEach(System.out::println);

    }

}


/*


java 8 支持所谓的函数式

1. 函数式接口
    如果一个接口, 只有一个抽象方法

2. lambda 表达式


3. 函数引用
    普通函数引用 类名::方法名
    构造函数引用  类名::new

4. Stream

 */