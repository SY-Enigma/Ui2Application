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
            // ????????????student?????????InsertActivity
            Bundle bundle = new Bundle();
            bundle.putSerializable("student", currStudent); // Student???????????????
            intent.putExtra("flag", 1);
            intent.putExtras(bundle);
            launcher.launch(intent);
        } else if (view.getId() == R.id.btn_del) {
            if (currStudent == null) {
                Toast.makeText(this, "???????????????", Toast.LENGTH_SHORT).show();
                return;
            }
            new AlertDialog.Builder(this).setTitle("??????").setMessage("???????????????")
                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // ????????????
                            dao.delete(currStudent.getId());
                            dialog.dismiss();
                            // ??????RecyclerView??????
                            changeData();
                        }
                    })
                    .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    // ??????????????????
    private void changeData() {
        students.clear();
        students.addAll(dao.selectAll());
        adapter.notifyDataSetChanged();
    }

    // ???assets????????????db????????????/data/data/??????/databases/??????????????????
    private void saveDBFile(String dbName) {
        String destPath = "/data" + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + getPackageName()
                + File.separator + "databases";
        File filePath = new File(destPath);
        // ????????????????????????
        if (!filePath.exists()) {
            filePath.mkdirs();
        }

        // ???????????????????????????
        File file = new File(destPath, dbName);
        try {
            // ??????????????????????????????
            InputStream input = this.getAssets().open(dbName);
            FileOutputStream output = new FileOutputStream(file);

            // ???????????????????????????????????????????????????????????????????????????
            int len = -1;
            byte[] buffer = new byte[1024];

            while ((len = input.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }
            output.flush();

            // ????????????????????????
            output.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}