package com.example.sqllsi;

import java.io.Serializable;

public class Student implements Serializable {
    private int _id;
    private String name;
    private String classmate;
    private int age;

    public Student(int _id, String name, String classmate, int age) {
        this._id = _id;
        this.name = name;
        this.classmate = classmate;
        this.age = age;
    }

    public Student(String toString, String toString1, int parseInt) {
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassmate() {
        return classmate;
    }

    public void setClassmate(String classmate) {
        this.classmate = classmate;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
