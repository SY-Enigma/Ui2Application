package com.example.mysql;

import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    private  final static String CREATE_TABLE_STUDENT = "create table t_student(id integer primary key " +
            "autoincrement," +
            "name varchar(20),classmate varchar(30),age integer)";
    private  final static  String DROP_TABLE_STUDENT = "drop table if exists t_student";



    public DBHelper(@Nullable Context context) {
        super(context, "student.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBHelper.CREATE_TABLE_STUDENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DBHelper.DROP_TABLE_STUDENT);
        onCreate(db);

    }
}
