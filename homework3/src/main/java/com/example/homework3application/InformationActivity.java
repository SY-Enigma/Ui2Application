package com.example.homework3application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class InformationActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private EditText etName;
    private  EditText etPhone;
    private RadioGroup rgSex;
    private LinearLayout mainLayout;
    private TextInputLayout phoneLayout;

    //
    private  String selected = "";
    private CheckBox cbAndroid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        // 初始化布局对象，作为Snackbar的上下文
        mainLayout =findViewById(R.id.ll_main);
        phoneLayout = findViewById(R.id.phone_layout);

        //初始化输入框、单选框
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        rgSex = findViewById(R.id.sex);

        //初始化复选框
        CheckBox cbCh1 = findViewById(R.id.ch1);
        CheckBox cbCh2 = findViewById(R.id.ch2);
        CheckBox cbCh3 = findViewById(R.id.ch3);
        CheckBox cbCh4 = findViewById(R.id.ch4);


        //获取按钮对象，设置它的点击事件监听器
        Button btnLogin1 = findViewById(R.id.btn_login1);

        //设置事件监听器
        cbCh1.setOnCheckedChangeListener(this);
        cbCh2.setOnCheckedChangeListener(this);
        cbCh3.setOnCheckedChangeListener(this);
        cbCh4.setOnCheckedChangeListener(this);
        btnLogin1.setOnClickListener(this);

        //获取传递的值
        final Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        if (!TextUtils.isEmpty(name)){
            etName.setText(name);
        }

        CheckBox checkBox1 = findViewById(R.id.checkbox1);

        checkRemember();


    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
        CheckBox checkBox = (CheckBox) buttonView;
        if(isChecked){
            selected += checkBox.getText().toString() +" ";
        }else {
            selected =  selected.replace(checkBox.getText().toString()+ ",","");

        }
        Snackbar.make(mainLayout, selected,Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void  onClick(View v){
        //获取输入的值
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        //验证手机号
        if(!validatePhone(phone)){
            phoneLayout.setError("请输入正确的手机号");
            etPhone.setText("");
            etPhone.requestFocus();
            return;
        }

        int id = rgSex.getCheckedRadioButtonId();
        RadioButton btn = findViewById(id);
        String sex = btn.getText().toString();

//        String sex = "男";
//        int id = rgSex.getCheckedRadioButtonId();
//        if (id == R.id.sex){
//            sex = "女";
//        }

        //将数据组合成字符串
        String info = "用户名:" + name + ", 手机号 :" + phone + ",性别 :" + sex +"\n喜欢的课程:" + selected;

        //使用Snackbar显示信息

        Snackbar.make(mainLayout,info, Snackbar.LENGTH_LONG).setAction("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InformationActivity.this,"信息确认",Toast.LENGTH_SHORT).show();
            }
        }).show();

    }

    private  static  final String PHONE_PATTERN = "^1[3-9]\\d{9}$";
    private  boolean validatePhone(String phone){
        Pattern pattern= Pattern.compile(PHONE_PATTERN);
        Matcher matcher = pattern.matcher(phone);
        return  matcher.matches();
    }

    private void remember(String username, String phone){

        try( FileOutputStream fos = openFileOutput("user.txt",Context.MODE_PRIVATE)) {
            fos.write((username +","+ phone).getBytes());


        }catch (IOException e){
            e.printStackTrace();
        }

    }


    private void checkRemember(){
        try {
            FileInputStream fis = openFileInput("user.txt");
            if (fis.available() == 0){
                Toast.makeText(this,"文件内容为空",Toast.LENGTH_SHORT).show();
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String[] datas = reader.readLine().split(",");
            if(datas.length > 1) {
                etName.setText(datas[0]);
                etPhone.setText(datas[1]);
            }
            fis.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    private void  clear(){
        try {
            // 清除文件内容
            FileOutputStream fos = openFileOutput("user.txt", Context.MODE_PRIVATE);
            fos.write("".getBytes());
            fos.close();

            // 直接删除文件
            deleteFile("user.txt");
        }catch (IOException e) {
            e.printStackTrace();
        }

    }
}