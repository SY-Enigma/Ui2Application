package com.example.feli;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.feli.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main3Activity extends AppCompatActivity {
    private static final String TAG = "Main3Activity";
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initPermission();
    }

    private void init() {
        // 读取SD卡的图片文件
        binding.btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // readLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                readPicture();
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writePicture();
            }
        });

        binding.pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                albumLauncher.launch("image/*");
            }
        });
    }

    // 读取图片的动态权限申请的启动器
    private final ActivityResultLauncher<String> readLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {
                        readPicture();
                        Toast.makeText(Main3Activity.this, "读取成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Main3Activity.this, "权限申请被拒绝", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    // 作用域存储，需要借助MediaStore API获取图片的Uri
    private void readPicture() {
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.MediaColumns.DATE_ADDED + " desc");
        if (cursor != null) {
            if (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
                Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                try {
                    loadPicture(uri);
                    Toast.makeText(Main3Activity.this, "读取成功", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "获取或解析图片失败，请检查权限", Toast.LENGTH_SHORT).show();
                }
            }
            cursor.close();
        }
    }

    // 图片写入SD卡，针对Android 11的存储变更，使用MediaStore API项共享存储写入媒体文件，不需要申请权限
    private void writePicture() {
        String displayName = System.currentTimeMillis() + ".png";
        // 创建 ContentValues数据对象
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/*");
        // 根据Android版本选择存储路径
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
        } else {
            values.put(MediaStore.MediaColumns.DATA,
                    Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_PICTURES + "/" + displayName);
        }
        // 使用 ContentResolver插入数据
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (uri != null) {
            try {
                // 获取输出流对象，插入数据
                OutputStream os = getContentResolver().openOutputStream(uri);
                if (os != null) {
                    BitmapDrawable drawable = (BitmapDrawable) binding.pic.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.flush();
                    os.close();
                    Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 从相册获取图片的启动器
    private final ActivityResultLauncher<String> albumLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri == null) {
                        Log.d(TAG, "uri为null");
                        return;
                    }
                    try {
                        loadPicture(uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(Main3Activity.this, "获取或解析图片失败，请检查权限", Toast.LENGTH_SHORT).show();
                    }
                    // Glide.with(MainResultApiActivity.this).load(uri).into(binding.ivPic);
                }
            });

    // 显示图片
    private void loadPicture(Uri uri) throws IOException {
        ParcelFileDescriptor fileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        if (fileDescriptor != null) {
            final Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor());
            fileDescriptor.close();
            binding.pic.setImageBitmap(bitmap);
        }
    }

    // permissions数组存放所有需要申请的权限
    String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    // 被禁止的权限集合
    List<String> deniedPermissions = new ArrayList<>();

    // 启动时的权限判断及申请
    private void initPermission() {
        deniedPermissions.clear();//清空已经允许的没有通过的权限
        //逐个判断是否还有未通过的权限
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(permission);//添加还未授予的权限到mPermissionList中
            }
        }
        //申请权限
        if (deniedPermissions.size() > 0) {//有权限没有通过，需要申请
            permissionLauncher.launch(permissions);
        } else {
            //权限已经都通过了，可以将程序继续打开了
            init();
        }
    }

    // 统一多权限申请的启动器
    ActivityResultLauncher<String[]> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    boolean isDismiss = false; //有权限没有通过
                    for (String key : result.keySet()) {
                        if (result.get(key) != null && !result.get(key)) {
                            isDismiss = true;
                            break;
                        }
                    }
                    if (isDismiss) { //有未被允许的权限
                        showPermissionDialog();
                    } else {
                        //权限全部通过后初始化
                        init();
                    }
                }
            });

    /**
     * 不再提示权限时的展示对话框
     */
    AlertDialog permissionDialog = null;
    String packName = "com.example.externalstorage";

    private void showPermissionDialog() {
        if (permissionDialog == null) {
            permissionDialog = new AlertDialog.Builder(this)
                    .setMessage("已禁用权限，请手动授予")
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            permissionDialog.cancel();

                            Uri packageURI = Uri.parse("package:" + packName);
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //关闭页面或者做其他操作
                            permissionDialog.cancel();
                            Main3Activity.this.finish();
                        }
                    })
                    .create();
        }
        permissionDialog.show();
    }
}