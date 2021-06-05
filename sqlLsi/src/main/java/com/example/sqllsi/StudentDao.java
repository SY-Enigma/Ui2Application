package com.example.sqllsi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class StudentDao {
    private DBHelper dbHelper;
    public StudentDao(Context context) {
        dbHelper = new DBHelper(context);
    }
    // 插入一条数据
    public void insert(Student student) {
        // 打开数据库
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // 第1种写法
        // 封装数据
        ContentValues values = new ContentValues();
        values.put("name", student.getName());
        values.put("classmate", student.getClassmate());
        values.put("age", student.getAge());
        db.insert("t_student", null, values);

        // 第2种写法
        // String sql = "insert into t_student(name, classmate, age) values(?,?,?)";
        // db.execSQL(sql, new String[]{student.getName(), student.getClassmate(), String.valueOf(student.getAge())});

        // 关闭数据库
        db.close();
    }
    // 插入一条数据
    public void insert(String name, String classmate, int age) {
        // 打开数据库
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // 第1种写法
        // 封装数据
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("classmate", classmate);
        values.put("age", age);
        db.insert("t_student", null, values);

        // 第2种写法
        // String sql = "insert into t_student(name, classmate, age) values(?,?,?)";
        // db.execSQL(sql, new String[]{student.getName(), student.getClassmate(), String.valueOf(student.getAge())});

        // 关闭数据库
        db.close();
    }

    // 更新数据
    public void update(Student student) {
        // 1. 打开数据库
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // 第1种写法
        // 2. 封装数据
        ContentValues values = new ContentValues();
        values.put("name", student.getName());
        values.put("classmate", student.getClassmate());
        values.put("age", student.getAge());

        // 3. 执行语句
        db.update("t_student", values, "_id=?",
                new String[]{String.valueOf(student.get_id())});

        // String sql = "update student set name=?, classmate=?, age=? where _id=?";
        // db.execSQL(sql, new String[]{student.getName(), student.getClassmate(),
        //         String.valueOf(student.getAge()), String.valueOf(student.get_id())});

        // 4. 关闭数据库
        db.close();
    }

    // 删除一条数据
    public void delete(int _id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("t_student", "_id=?", new String[]{String.valueOf(_id)});

        // String sql = "delete from student where _id=?";
        // db.execSQL(sql, new String[]{String.valueOf(_id)});
        db.close();
    }

    // 查询所有数据
    public List<Student> selectAll() {
        List<Student> students = new ArrayList<>();
        // 1. 打开数据库
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // select ... from table where ... group by ... having ... order by ...
        // 2. 查询
        Cursor cursor = db.query("t_student", null, null, null,
                null, null, null);
        // 3. 将查询结果转为List
        while (cursor.moveToNext()) {
            Student student = new Student(
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("classmate")),
                    cursor.getInt(cursor.getColumnIndex("age")));
            student.set_id(cursor.getInt(cursor.getColumnIndex("_id")));

            students.add(student);
        }
        // 4. 关闭数据库
        cursor.close();
        db.close();
        // 5. 返回结果
        return students;
    }

    // 查询一条数据
    public Student select(int _id) {
        Student student = null;

        // 1. 打开数据库
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // select ... from table where ... group by ... having ... order by ...
        // 2. 查询
        Cursor cursor = db.query("t_student", null, "_id=?",
                new String[]{String.valueOf(_id)}, null, null, null);

        // Cursor cursor = db.rawQuery("select * from t_student where _id=?",
        //        new String[]{String.valueOf(_id)});

        // 3. 获取查询结果
        if (cursor.moveToNext()) {
            student = new Student(cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("classmate")),
                    cursor.getInt(cursor.getColumnIndex("age")));
            student.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
        }
        // 4. 关闭数据库
        cursor.close();
        db.close();
        return student;
    }

    // 条件查询
    public List<Student> selectByCondition(String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            return selectAll();
        }
        List<Student> students = new ArrayList<>();
        // 1. 打开数据库
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // select ... from table where ... group by ... having ... order by ...
        // 2. 查询
        Cursor cursor = db.query("t_student", null, "name like ? or classmate like ? or age=?",
                new String[]{"%" + keyword + "%", "%" + keyword + "%", keyword}, null, null, null);
        // 3. 将查询结果转为List
        while (cursor.moveToNext()) {
            Student student = new Student(
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("classmate")),
                    cursor.getInt(cursor.getColumnIndex("age")));
            student.set_id(cursor.getInt(cursor.getColumnIndex("_id")));

            students.add(student);
        }
        // 4. 关闭数据库
        cursor.close();
        db.close();
        // 5. 返回结果
        return students;
    }


}
