package com.atuigu;

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/18 16:40
 */
public class Demo2 {
    public static void main(String... args) {
        //  foo(1, 3, 2);
        Integer[] ns = new Integer[]{1, 2, 3};
        foo( ns);
    }

    public static void foo(Object... n) {
        for (Object i : n) {
            System.out.println(i);
        }
    }
}
