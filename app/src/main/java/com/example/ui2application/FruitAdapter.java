package com.example.ui2application;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ui2application.databinding.ItemFruitBinding;

import java.util.List;

public class FruitAdapter extends RecyclerView.Adapter<FruitAdapter.ViewHolder> {
    private final List<Fruit> fruits;
    private final View.OnClickListener listener;

    // 构造方法，接收传递的数据集合
    public FruitAdapter(List<Fruit> fruits, View.OnClickListener listener) {
        this.listener = listener;
        this.fruits = fruits;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 加载列表项布局
        ItemFruitBinding binding = ItemFruitBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        // 卡片式布局
//        ItemFruitCardBinding binding = ItemFruitCardBinding.inflate(
//                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 给列表项控件赋值
        Fruit fruit = fruits.get(position);
        holder.binding.tvName.setText(fruit.getTitle());
        holder.binding.ivFruit.setImageResource(fruit.getImageId());

        // 设置item的选中与否状态
        holder.itemView.setSelected(selectedIndex == position);
    }

    @Override
    public int getItemCount() {
        return fruits.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemFruitBinding binding;

        public ViewHolder(@NonNull ItemFruitBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.itemView.setTag(this);
            this.itemView.setOnClickListener(listener);
            this.itemView.setOnLongClickListener(longClickListener);
        }
    }

    //记录当前选中的条目索引，用于设置背景色
    private int selectedIndex;

    public void setSelectedIndex(int position) {
        this.selectedIndex = position;
        notifyDataSetChanged();
    }

    // 添加数据
    public void addData(int position, Fruit fruit) {
        this.fruits.add(position, fruit);
        notifyItemInserted(position);
    }

    // 删除数据
    public void removeData(int position) {
        this.fruits.remove(position);
        notifyDataSetChanged();
    }

    private View.OnLongClickListener longClickListener;

    public void setOnLongClickListener(View.OnLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }
}
