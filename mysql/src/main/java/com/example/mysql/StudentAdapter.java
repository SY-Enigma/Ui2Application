package com.example.mysql;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mysql.databinding.ItemStudentBinding;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {
    private List<Student> students ;
    private ItemStudentBinding binding;

    public StudentAdapter(List<Student> students) {
        this.students= students;

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemStudentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Student student = students.get(position);
        holder.binding.tvName.setText(student.getName());
        holder.binding.tvClassmate.setText(student.getClassmate());
        holder.binding.tvAge.setText(String.valueOf(student.getAge()));

        // 设置item的选中与否状态
        holder.itemView.setSelected(selectedIndex == position);
    }

    //记录当前选中的条目索引
    private int selectedIndex;
    public void setSelectedIndex(int position) {
        this.selectedIndex = position;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    private View.OnClickListener onClickListener = null;
    private View.OnLongClickListener onLongClickListener = null;
    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemStudentBinding binding;

        public ViewHolder(ItemStudentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.itemView.setTag(this);
            this.itemView.setOnClickListener(onClickListener);
            this.itemView.setOnLongClickListener(onLongClickListener);
        }
    }
}