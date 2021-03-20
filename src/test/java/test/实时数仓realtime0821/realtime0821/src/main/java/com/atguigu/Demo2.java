package com.atguigu;

interface Call {
    void call(Dog dog);
}

interface I1 {
    Dog call();
}

/**
 * @Author lizhenchao@atguigu.cn
 * @Date 2021/1/19 14:54
 */
public class Demo2 {
    public static void main(String[] args) {
        //        foo(dog -> dog.speak());
        // foo(Dog::speak);  // 函数引用 类名::方法名

        //        List().foreach(x => println(x))
        //List().foreach(println)
//        f1(() -> new Dog());
        f1(Dog::new);

    }

    public static void foo(Call c) {
        c.call(new Dog());
    }

    public static void f1(I1 i) {
         i.call();

    }
}

class Dog {
    public void speak() {
        System.out.println("dog speak..");
    }

    public void speak1() {
        System.out.println("dog speak..");
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