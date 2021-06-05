package com.example.room;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private List<Student> students;
    private Student currentStudent;
    private StudentAdapter adapter;
    private com.example.room.databinding.ActivityMainBinding binding;
    private StudentReposity studentReposity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.example.room.databinding.ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 获取数据库的数据
        // 获取数据库的数据
        studentReposity = new StudentReposity(this.getApplication());
        students = studentReposity.selectAll();

        // 初始化控件
        initView();
    }

    private void initView() {
        // 设置按钮监听器
        binding.btnAdd.setOnClickListener(this);
        binding.btnUpdate.setOnClickListener(this);
        binding.btnDelete.setOnClickListener(this);

        // RecyclerView控件的初始化、设置布局管理器和动画
        binding.rvStudents.setLayoutManager(new LinearLayoutManager(this));
        binding.rvStudents.setItemAnimator(new DefaultItemAnimator());
        // 设置 RecyclerView控件的Adapter
        adapter = new StudentAdapter(students);
        binding.rvStudents.setAdapter(adapter);

        // adapter添加item的点击事件的监听
        adapter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                StudentAdapter.ViewHolder viewHolder = (StudentAdapter.ViewHolder) v.getTag();
                int position = viewHolder.getAdapterPosition();
                adapter.setSelectedIndex(position);
                currentStudent = students.get(position);
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
            bundle.putSerializable("student", currentStudent); // Student类需序列化
            intent.putExtra("flag", 1);
            intent.putExtras(bundle);
            launcher.launch(intent);
        } else if (view.getId() == R.id.btn_delete) {
            new AlertDialog.Builder(this).setTitle("删除").setMessage("确认删除？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 删除数据
                            studentReposity.delete(currentStudent);
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
        students.addAll(studentReposity.selectAll());
        adapter.notifyDataSetChanged();
    }
}