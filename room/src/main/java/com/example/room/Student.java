package com.example.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

// 实体类，与数据库表字段一一对应
@Entity(tableName = "t_student")
public class Student implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private int id;

    @ColumnInfo
    private String name;
    @ColumnInfo
    private String classmate;
    @ColumnInfo
    private int age;

    public Student() {
    }

    public Student(String name, String classmate, int age) {
        this.name = name;
        this.classmate = classmate;
        this.age = age;
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