package com.atguigu.java5;

/*
    JavaBean:
 */
public class User {
    private int id;
    private String name;

    public User(){

    }

    public User(int id, String name){
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return id + "===" + name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
