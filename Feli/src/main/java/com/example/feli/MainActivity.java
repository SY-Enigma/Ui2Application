package com.example.feli;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.feli.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private static final String TAG = "MainActivity";
    private static final String FILE_NAME = "11.png";
    private static final int REQUEST_PERMISSIONS = 0;

    private static final int REQUEST_READ = 1;
    private static final int REQUEST_WRITE = 2;
    private static final int REQUEST_PHOTO = 3;
    private static final int SELECT_PHOTO = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        // 从SD卡读取指定文件名的文件
        binding.btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 申请读SD的权限，要求android的版本大于6.0（Build.VERSION_CODES.M）
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ);
                        return;
                    }
                }
                readPicture(MainActivity.FILE_NAME);
            }
        });

        // 将图片存入SD卡
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writePicture();
            }
        });

        // 点击图片，从相册选择图片
        binding.pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAlbum();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0 || grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "权限申请被拒绝", Toast.LENGTH_SHORT).show();
            return;
        }
        if (requestCode == REQUEST_READ) {
            readPicture(MainActivity.FILE_NAME);
        }
    }


    // 图片写入SD卡，针对Android 11的存储变更，使用MediaStore API项共享存储写入媒体文件，不需要申请权限
    private void writePicture() {
        String displayName = System.currentTimeMillis() + ".png";
        // 获取外部存储的Pictures目录，创建存储文件
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!path.exists() && !path.mkdirs()) {
            Log.e(TAG, "图片目录不能创建");
            return;
        }
        File file = new File(path, displayName);
        try {
            if (file.createNewFile()) {
                // 获取ImageView的Bitmap图片对象
                BitmapDrawable drawable = (BitmapDrawable) binding.pic.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                // 将Bitmap对象写入SD卡
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                // 关闭输出流
                fos.flush();
                fos.close();
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // 从sd卡的pictures目录读取图片
    private void readPicture(String fileName) {
        // 读取SD卡上的文件
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!path.exists()) {
            Toast.makeText(this, "文件路径不存在", Toast.LENGTH_SHORT).show();
        }
        File file = new File(path, fileName);
        try {
            // 创建file的文件输入流
            // /storage/emulated/0/Pictures/flower.jpg
            FileInputStream fis = new FileInputStream(file);
            // 将文件流写入imageview
            binding.pic.setImageBitmap(BitmapFactory.decodeStream(fis));
            // 关闭输入流
            fis.close();
            Toast.makeText(MainActivity.this, "读取成功", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "文件不存在或读取失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void openAlbum() {
        final Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SELECT_PHOTO && data != null) {
            final Uri uri = data.getData();
            if (uri != null) {
                try {
                    loadPicture(uri);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "解析图片失败，请检查权限", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "获取图片失败，请检查权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 显示图片
    private void loadPicture(Uri uri) throws IOException {
        ParcelFileDescriptor fileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        if (fileDescriptor != null) {
            final Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor());
            fileDescriptor.close();
            binding.pic.setImageBitmap(bitmap);
        }
    }

    // 将assets目录下的db文件写入/data/data/包名/databases/数据库文件名
    private void saveDBFile(String dbName) {
        String destPath = "/data" + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + getPackageName()
                + File.separator + "databases";
        File filePath = new File(destPath);
        // 判断目录是否存在
        if (!filePath.exists()) {
            filePath.mkdirs();
        }

        // 创建目标目录的文件
        File file = new File(destPath, dbName);
        try {
            // 创建输入、输出流对象
            InputStream input = this.getAssets().open(dbName);
            FileOutputStream output = new FileOutputStream(file);

            // 将输入流的数据写入输出流（二进制流文件的通用写法）
            int len = -1;
            byte[] buffer = new byte[1024];

            while ((len = input.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }
            output.flush();

            // 关闭输入、输出流
            output.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}