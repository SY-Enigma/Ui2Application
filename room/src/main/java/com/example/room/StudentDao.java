package com.example.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface StudentDao {
    // 插入一条数据
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Student student);

    // 更新数据
    @Update
    void update(Student student);

    // 删除一条数据
    @Delete
    void delete(Student student);

    // 查询所有数据
    @Query("SELECT * FROM t_student")
    List<Student> selectAll();

    // 查询一条数据
    @Query("SELECT * FROM t_student WHERE _id=:id")
    Student select(int id);
}