package com.atguigu.java;

/*
    泛型
 */
public class GenericDemo {
    public static void main(String[] args) {


        SubClass sc = new SubClass();
    }
}


//自定义泛型类
class Student<T>{

    T t;

    public void setT(T t){
        this.t = t;
    }

    public T getT(){
        return t;
    }
}
/*

    设置父类的泛型类型 ：在继承父类时就指定父类的泛型类型

 */
class SubClass extends Student<String>{

}

/*
    设置父类的泛型类型 ：在创建子类对象的时候指定父类的泛型类型。
 */
class SubClass2<T> extends Student<T>{

}
