package com.example.room;

import android.app.Application;

import java.util.List;

public class StudentReposity {
    private final StudentDao studentDao;
    public StudentReposity(Application application) {
        InfoRoomDatabase db = InfoRoomDatabase.getInstance(application);
        studentDao = db.getStudentDao();
    }
    // 在子线程中执行添加操作
    public void insert(final Student student) {
        InfoRoomDatabase.writeExecutor.execute(new Runnable() {
            @Override
            public void run() {
                studentDao.insert(student);
            }
        });
    }
    // 在子线程中执行更新操作
    public void update(final Student student) {
        InfoRoomDatabase.writeExecutor.execute(new Runnable() {
            @Override
            public void run() {
                studentDao.update(student);
            }
        });
    }
    // 在子线程中执行删除操作
    public void delete(final Student student) {
        InfoRoomDatabase.writeExecutor.execute(new Runnable() {
            @Override
            public void run() {
                studentDao.delete(student);
            }
        });
    }
    // 查询所有
    public List<Student> selectAll() {
        return studentDao.selectAll();
    }
    // 查询单个
    public Student select(int id) {
        return studentDao.select(id);
    }
}