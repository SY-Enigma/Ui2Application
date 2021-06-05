package com.example.mysql;

import java.io.Serializable;

public class Student implements Serializable {
    private  int  id;
    private  String name;
    private  int  age;
    private  String classmate;

    public Student() {

    }

    public Student(int id, String name, int age, String classmate) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.classmate = classmate;
    }

    public Student(String name, String classmate, int age) {
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getClassmate() {
        return classmate;
    }

    public void setClassmate(String classmate) {
        this.classmate = classmate;
    }

    public void set_id(int id) {
    }

    public void add(Student student) {
    }
}
