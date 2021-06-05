package com.example.mysql;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import android.widget.Toast;


import com.example.mysql.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainBinding binding;
    private List<Student> students;
    private  StudentDao dao;
    private  Student currStudent;
    private StudentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        binding.getRoot();

       dao = new StudentDao(this);
       students = dao.selectAll();
        if (students.size() > 0) {
            currStudent = students.get(0);
        }
       initView();
    }

    private void initView() {
        binding.btnAdd.setOnClickListener(this);
        binding.btnDel.setOnClickListener(this);
        binding.btnUpdate.setOnClickListener(this);

        binding.rvStudent.setLayoutManager(new LinearLayoutManager(this));
        binding.rvStudent.setItemAnimator(new DefaultItemAnimator());

        adapter = new StudentAdapter(students);
        binding.rvStudent.setAdapter(adapter);

        adapter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                StudentAdapter.ViewHolder viewHolder = (StudentAdapter.ViewHolder) v.getTag();
                int position = viewHolder.getAdapterPosition();
                adapter.setSelectedIndex(position);
                currStudent = students.get(position);
            }
        });
    }

    private ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    changeData();
                }
            });

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, InsertActivity.class);
        if (view.getId() == R.id.btn_add) {
            launcher.launch(intent);
        } else if (view.getId() == R.id.btn_update) {
            // 将选中的student传递给InsertActivity
            Bundle bundle = new Bundle();
            bundle.putSerializable("student", currStudent); // Student类需序列化
            intent.putExtra("flag", 1);
            intent.putExtras(bundle);
            launcher.launch(intent);
        } else if (view.getId() == R.id.btn_del) {
            if (currStudent == null) {
                Toast.makeText(this, "没选中数据", Toast.LENGTH_SHORT).show();
                return;
            }
            new AlertDialog.Builder(this).setTitle("删除").setMessage("确认删除？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 删除数据
                            dao.delete(currStudent.getId());
                            dialog.dismiss();
                            // 刷新RecyclerView列表
                            changeData();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    // 重新装载数据
    private void changeData() {
        students.clear();
        students.addAll(dao.selectAll());
        adapter.notifyDataSetChanged();
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