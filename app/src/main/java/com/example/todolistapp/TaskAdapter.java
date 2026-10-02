package com.example.todolistapp;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final List<Task> taskList;
    private final OnTaskActionListener listener;

    public interface OnTaskActionListener {
        void onEdit(int position);
        void onDelete(int position);
        void onTaskChecked();
    }
    public TaskAdapter(List<Task> taskList, OnTaskActionListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);

        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {

        Task task = taskList.get(position);

        holder.taskText.setText(task.getTaskText());
        holder.taskCheckBox.setChecked(task.isCompleted());

        updateTaskAppearance(holder.taskText, task.isCompleted());

        holder.taskCheckBox.setOnCheckedChangeListener(null);

        holder.taskCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            updateTaskAppearance(holder.taskText, isChecked);
            listener.onTaskChecked();
        });

        holder.editButton.setOnClickListener(v -> {
            int currentPosition = holder.getBindingAdapterPosition();

            if (currentPosition != RecyclerView.NO_POSITION) {
                listener.onEdit(currentPosition);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            int currentPosition = holder.getBindingAdapterPosition();

            if (currentPosition != RecyclerView.NO_POSITION) {
                listener.onDelete(currentPosition);
            }
        });
    }

    private void updateTaskAppearance(TextView textView, boolean completed) {

        if (completed) {
            textView.setPaintFlags(
                    textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
            );
            textView.setAlpha(0.5f);
        } else {
            textView.setPaintFlags(
                    textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)
            );
            textView.setAlpha(1.0f);
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        CheckBox taskCheckBox;
        TextView taskText;
        Button editButton;
        Button deleteButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            taskCheckBox = itemView.findViewById(R.id.taskCheckBox);
            taskText = itemView.findViewById(R.id.taskText);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}