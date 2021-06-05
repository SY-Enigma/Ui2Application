package com.example.sqllsi;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.example.sqllsi.databinding.ActivityMainRefreshBinding;

import java.util.List;

public class MainRefreshActivity extends AppCompatActivity {
    private List<Student> students;
    private StudentDao dao;
    private Student currentStudent;
    private StudentAdapter adapter;
    private ActivityMainRefreshBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainRefreshBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        // 获取数据库的数据
        dao = new StudentDao(this);
        students = dao.selectAll();

        // 初始化控件
        initView();
    }

    private void initView() {
        // RecyclerView控件的初始化、设置布局管理器和动画
        binding.rvStudents.setLayoutManager(new LinearLayoutManager(this));
        binding.rvStudents.setItemAnimator(new DefaultItemAnimator());

        // 设置 RecyclerView控件的Adapter
        adapter = new StudentAdapter(students);
        binding.rvStudents.setAdapter(adapter);

        // adapter添加item的点击事件的监听
        adapter.setOnClickListener(view -> {
            StudentAdapter.ViewHolder viewHolder = (StudentAdapter.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            adapter.setSelectedIndex(position);
            currentStudent = students.get(position);

            Intent intent = new Intent(MainRefreshActivity.this, InsertActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("student", currentStudent); // Student类需序列化
            intent.putExtra("flag", 1);
            intent.putExtras(bundle);
            launcher.launch(intent);
        });
        adapter.setOnLongClickListener(v -> {
            StudentAdapter.ViewHolder viewHolder = (StudentAdapter.ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();
            currentStudent = students.get(position);
            new AlertDialog.Builder(MainRefreshActivity.this)
                    .setTitle("删除").setMessage("确认删除？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        // 删除数据
                        dao.delete(currentStudent.get_id());
                        dialog.dismiss();
                        // 刷新RecyclerView列表
                        changeData();
                    })
                    .setNegativeButton("取消", (dialog, which) -> dialog.dismiss()).show();
            return true;
        });
        // 下拉刷新
        binding.refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        binding.refreshLayout.setOnRefreshListener(() -> {
            changeData();
            binding.refreshLayout.setRefreshing(false);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_student, menu);

        // 获取SearchView对象
        final MenuItem item = menu.findItem(R.id.item_search);
        SearchView searchView = (SearchView) item.getActionView();
        // 设置提交按钮可见
        searchView.setSubmitButtonEnabled(true);
        // 搜索框View，设置样式
        SearchView.SearchAutoComplete searchEditView = searchView.findViewById(R.id.search_src_text);
        searchEditView.setHint("请输入搜索内容");
        searchEditView.setHintTextColor(getResources().getColor(R.color.selectEditViewBackground));
        searchEditView.setTextColor(getResources().getColor(R.color.colorWhite));
        searchEditView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        // 去掉搜索框默认的下划线
        LinearLayout searchPlate = searchView.findViewById(R.id.search_plate);
        searchPlate.setBackground(null);

        // 监听文本变化
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 提交文本时调用
                List<Student> queryResult = dao.selectByCondition(query);
                students.clear();
                students.addAll(queryResult);
                adapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 文本搜索框发生变化时调用
                List<Student> queryResult = dao.selectByCondition(newText);
                students.clear();
                students.addAll(queryResult);
                adapter.notifyDataSetChanged();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.item_add) {
            Intent intent = new Intent(this, InsertActivity.class);
            launcher.launch(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> changeData());

    // 重新装载数据
    private void changeData() {
        students.clear();
        students.addAll(dao.selectAll());
        adapter.notifyDataSetChanged();
    }
}