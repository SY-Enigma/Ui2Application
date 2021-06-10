package com.example.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class StudentProvider extends ContentProvider {
    public static final int STUDENT_DIR = 0;
    public static final int STUDENT_ITEM = 1;
    public static final int CLASSMATE_DIR = 2;
    public static final int CLASSMATE_ITEM = 3;
    public static final String AUTHORITY = "com.example.provider";

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "student", STUDENT_DIR);
        uriMatcher.addURI(AUTHORITY, "student/#", STUDENT_ITEM);
        uriMatcher.addURI(AUTHORITY, "classmate", CLASSMATE_DIR);
        uriMatcher.addURI(AUTHORITY, "classmate/#", CLASSMATE_ITEM);
    }
    // SQLiteDatabase必须声明为成员变量
    private SQLiteDatabase db;

    public StudentProvider() {
    }

    @Override
    public boolean onCreate() {
        DBHelper dbHelper = new DBHelper(getContext());
        db = dbHelper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;

        switch(uriMatcher.match(uri)) {
            case STUDENT_DIR:
                // 查询t_student表的所有数据
                cursor = db.query("t_student", projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case STUDENT_ITEM:
                // 查询t_student表的单条数据
                String id = uri.getPathSegments().get(1);
                cursor = db.query("t_student", projection, "_id=?", new String[]{id},
                        null, null, sortOrder);
                break;
            case CLASSMATE_DIR:
                // 查询t_classmate表的所有数据
                cursor = db.query("t_classmate", projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case CLASSMATE_ITEM:
                // 查询t_classmate表的单条数据
                id = uri.getPathSegments().get(1);
                cursor = db.query("t_classmate", projection, "_id=?", new String[]{id},
                        null, null, sortOrder);
                break;
        }
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        Uri newUri = null;
        long newId = 0;

        switch (uriMatcher.match(uri)) {
            case STUDENT_DIR:
            case STUDENT_ITEM:
                newId = db.insert("t_student", null, contentValues);
                newUri = Uri.parse("content://" + AUTHORITY + "/student/" + newId);
                break;
            case CLASSMATE_DIR:
            case CLASSMATE_ITEM:
                newId = db.insert("t_classmate", null, contentValues);
                newUri = Uri.parse("content://" + AUTHORITY + "/classmate/" + newId);
                break;
        }
        return newUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String select, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case STUDENT_DIR:
                count = db.delete("t_student", select, selectionArgs);
                break;
            case STUDENT_ITEM:
                String id = uri.getPathSegments().get(1);
                count = db.delete("t_student", "_id=?", new String[]{id});
                break;
            case CLASSMATE_DIR:
                count = db.delete("t_classmate", select, selectionArgs);
                break;
            case CLASSMATE_ITEM:
                id = uri.getPathSegments().get(1);
                count = db.delete("t_classmate", "_id=?", new String[]{id});
                break;
        }
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues,
                      String select, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case STUDENT_DIR:
                count = db.update("t_student", contentValues, select, selectionArgs);
                break;
            case STUDENT_ITEM:
                String id = uri.getPathSegments().get(1);
                count = db.update("t_student", contentValues, "_id=?", new String[]{id});
                break;
            case CLASSMATE_DIR:
                count = db.update("t_classmate", contentValues, select, selectionArgs);
                break;
            case CLASSMATE_ITEM:
                id = uri.getPathSegments().get(1);
                count = db.update("t_classmate", contentValues, "_id=?", new String[]{id});
                break;
        }
        return count;
    }

    /**
     * 所有内容提供器必须提供的方法，用于获取Uri对象所对应的MIME类型
     * MIME字符串的组成：
     *      以vnd开头，
     *      内容以路径结尾，则后接android.cursor.dir/
     *      url以id结尾，则后接android.cursor.item/
     *      最后接上vnd.<authority>.<path>
     * @param uri
     * @return
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case STUDENT_DIR:
                return "vnd.android.cursor.dir/vnd.com.example.provider.student";
            case STUDENT_ITEM:
                return "vnd.android.cursor.item/vnd.com.example.provider.student";
            case CLASSMATE_DIR:
                return "vnd.android.cursor.dir/vnd.com.example.provider.classmate";
            case CLASSMATE_ITEM:
                return "vnd.android.cursor.item/vnd.com.example.provider.classmate";
        }
        return null;
    }
}