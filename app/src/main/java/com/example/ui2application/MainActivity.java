package com.example.ui2application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.example.ui2application.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FruitAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initData();
        initView();
    }

    private void initView() {
        // 设置布局管理器
        // 线性布局器
        binding.rvFruit.setLayoutManager(new LinearLayoutManager(this));
        // 2列的网格布局器
//        binding.rvFruit.setLayoutManager(new GridLayoutManager(this, 2));
        // 设置分割线
        binding.rvFruit.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        // 设置动画
        binding.rvFruit.setItemAnimator(new DefaultItemAnimator());
        // 设置适配器
        adapter = new FruitAdapter(fruits, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 设置item点击事件处理
                final FruitAdapter.ViewHolder viewHolder = (FruitAdapter.ViewHolder) view.getTag();
                final int position = viewHolder.getAdapterPosition();
                adapter.setSelectedIndex(position);
                final Fruit fruit = fruits.get(position);
                Snackbar.make(view, String.format(Locale.CHINA, "点击了第%d行的%s", position + 1,
                        fruit.getTitle()),
                        Snackbar.LENGTH_SHORT).show();
            }
        });
        binding.rvFruit.setAdapter(adapter);

        // 设置FAB的点击事件监听
        binding.fabAdd.setOnClickListener(view -> {
            Snackbar.make(view, "添加一个水果信息", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            adapter.addData(fruits.size(), new Fruit("新增苹果", R.drawable.apple));
        });


    //长按删除
        adapter.setOnLongClickListener(view -> {
            final FruitAdapter.ViewHolder viewHolder = (FruitAdapter.ViewHolder) view.getTag();
            final int position = viewHolder.getAdapterPosition();
            Snackbar.make(view, "确认删除?", Snackbar.LENGTH_LONG)
                    .setAction("删除", v -> adapter.removeData(position)).show(); //自定义删除数据的方法：删除fruits列表的position位置的数据，更新adapter

            return true;
        });
    }

    private final List<Fruit> fruits = new ArrayList<>();
    private final List<Fruit> fruitList = new ArrayList<Fruit>() {
        {
            add(new Fruit("苹果", R.drawable.apple));
            add(new Fruit("香蕉", R.drawable.banana));
            add(new Fruit("橙子", R.drawable.orange));
            add(new Fruit("樱桃", R.drawable.cherry));
            add(new Fruit("芒果", R.drawable.mango));
            add(new Fruit("梨子", R.drawable.pear));
            add(new Fruit("草莓", R.drawable.strawberry));
            add(new Fruit("菠萝", R.drawable.pineapple));
            add(new Fruit("西瓜", R.drawable.watermelon));
            add(new Fruit("葡萄", R.drawable.grape));
        }
    };

    // 初始化数据
    // 初始化数据
    private void initData() {
        fruits.clear();
        for (int i = 0; i < 10; i++) {
            fruits.addAll(fruitList);
        }
        Collections.shuffle(fruits);
    }
}