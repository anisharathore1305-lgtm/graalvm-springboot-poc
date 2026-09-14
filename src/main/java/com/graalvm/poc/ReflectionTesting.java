package com.graalvm.poc;

public class ReflectionTesting {

    private String name;
    private int age;

    public ReflectionTesting() {
    }

    public ReflectionTesting(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public void sayHello() {
        System.out.println("Hello, I am " + name + ", age " + age);
    }
}