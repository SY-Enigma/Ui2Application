package com.example.mysql;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.example.mysql.databinding.ActivityInsertBinding;

public class InsertActivity extends AppCompatActivity implements View.OnClickListener{
    private  ActivityInsertBinding binding;

    private  StudentDao studentDao = new StudentDao(this);
    private  Student currStudent;
    private  boolean isUpdate = false;//添加或更新的标识符


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        binding = ActivityInsertBinding.inflate(getLayoutInflater());
        binding.getRoot();

        binding.btn1.setOnClickListener(this);
        binding.btn2.setOnClickListener(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null){
            currStudent = (Student) bundle.get("student");
        }
        if (currStudent != null){
            isUpdate = true;
           binding.name.setText(currStudent.getName());
           binding.age.setText(String.valueOf(currStudent.getAge()));

            SpinnerAdapter spinnerAdapter = binding.tvSpinner.getAdapter();
            for (int i = 0; i < spinnerAdapter.getCount(); i++){
                if (spinnerAdapter.getItem(i).toString().equals(currStudent.getClassmate())){
                   binding.tvSpinner.setSelection(i);
                    break;
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn1){

                Student student = new Student(binding.name.getText().toString(),binding.tvSpinner.getSelectedItem().toString(),
                        Integer.parseInt(binding.age.getText().toString()));
                if (isUpdate){
                    student.set_id(currStudent.getId());
                    studentDao.update(student);
                }else {
                    studentDao.insert(student);
                }
                setResult(RESULT_OK,new Intent());
                finish();

        }else if (v.getId() == R.id.btn_2){
            finish();
        }
    }
}