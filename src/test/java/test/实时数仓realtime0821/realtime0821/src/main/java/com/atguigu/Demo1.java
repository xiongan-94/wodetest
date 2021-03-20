package com.atguigu;

interface Animal {
    int speak();

    default void say() {
        System.out.println("say");
    }

    static void foo() {
        System.out.println("foo");
    }
}

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/19 14:54
 */
public class Demo1 {
    public static void main(String[] args) {
        // foo(() -> System.out.println("speak..."));

        // foo(() -> System.out.println("speak..."));

        foo(() -> {
            //System.out.println("aaaa");
           return  10;
        });

        foo(new Animal() {
            @Override
            public int speak() {

                return 0;
            }
        });
    }

    public static void foo(Animal a) {
        a.speak();
    }

}



/*


java 8 支持所谓的函数式

1. 函数式接口
    如果一个接口, 只有一个抽象方法

2. lambda 表达式


3. 函数引用

4. Stream
 */