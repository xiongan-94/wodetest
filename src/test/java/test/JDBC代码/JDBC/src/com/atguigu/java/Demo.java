package com.atguigu.java;

public class Demo {
    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName("com.atguigu.java.Person");
    }
}

class Person{
    static {
        System.out.println("person==================");
    }
}
