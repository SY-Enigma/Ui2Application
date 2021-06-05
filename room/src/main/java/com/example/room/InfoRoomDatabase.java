package com.example.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Student.class}, version = 1, exportSchema = false)
public abstract class InfoRoomDatabase extends RoomDatabase {
    public static String DB_NAME = "info";
    private static volatile InfoRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    // 获取Dao的抽象方法
    public abstract StudentDao getStudentDao();
    // 数据库写操作的线程池
    public static final ExecutorService writeExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    // 单例模式
    public static InfoRoomDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (InfoRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            InfoRoomDatabase.class, InfoRoomDatabase.DB_NAME)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    // 清除Database实例
    public void cleanUp() {
        INSTANCE = null;
    }
}
