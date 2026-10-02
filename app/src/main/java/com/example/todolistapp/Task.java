package com.example.todolistapp;

public class Task {

    private String taskText;
    private boolean completed;

    public Task(String taskText) {
        this.taskText = taskText;
        this.completed = false;
    }

    public Task(String taskText, boolean completed) {
        this.taskText = taskText;
        this.completed = completed;
    }

    public String getTaskText() {
        return taskText;
    }

    public void setTaskText(String taskText) {
        this.taskText = taskText;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}