package com.example.sqllsi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SpinnerAdapter;

import com.example.sqllsi.databinding.ActivityInsertBinding;

public class InsertActivity extends AppCompatActivity implements View.OnClickListener {
    private StudentDao studentDao;
    private Student currentStudent;
    private boolean isUpdate = false; // 添加或更新的标识符
    private ActivityInsertBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInsertBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        studentDao = new StudentDao(this);

        // 设置监听器
        binding.btnConfirm.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);

        // 判断是否有数据需要加载
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null) {
            currentStudent = (Student) bundle.get("student");
        }
        // 控件加载数据
        if(currentStudent != null) {
            isUpdate = true;
            binding.etName.setText(currentStudent.getName());
            binding.etAge.setText(String.valueOf(currentStudent.getAge()));

            // 设置Spinner值
            SpinnerAdapter spinnerAdapter = binding.spClassmate.getAdapter();
            for(int i = 0; i < spinnerAdapter.getCount(); i++) {
                if(spinnerAdapter.getItem(i).toString()
                        .equals(currentStudent.getClassmate())) {
                    binding.spClassmate.setSelection(i);
                    break;
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_confirm) {
            // 将输入的数据封装成student对象
            Student student = new Student(
                    binding.etName.getText().toString(),
                    binding.spClassmate.getSelectedItem().toString(),
                    Integer.parseInt(binding.etAge.getText().toString()));
            // 插入数据
            if(isUpdate) {
                // 更新数据
                student.set_id(currentStudent.get_id());
                studentDao.update(student);
            } else {
                // 插入数据
                studentDao.insert(student);
            }
            // 返回MainActivity，刷新RecyclerView
            setResult(RESULT_OK, new Intent());
            finish();
        } else if (view.getId() == R.id.btn_cancel) {
            finish();
        }
    }
}