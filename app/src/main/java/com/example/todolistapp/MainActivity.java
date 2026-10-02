package com.example.todolistapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText taskInput;
    private Button addButton;
    private RecyclerView recyclerView;

    private ArrayList<Task> taskList;
    private TaskAdapter taskAdapter;

    private SharedPreferences sharedPreferences;

    private static final String PREFS_NAME = "TodoPreferences";
    private static final String TASKS_KEY = "tasks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Connect XML views
        taskInput = findViewById(R.id.taskInput);
        addButton = findViewById(R.id.addButton);
        recyclerView = findViewById(R.id.recyclerView);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(
                PREFS_NAME,
                MODE_PRIVATE
        );

        // Load previously saved tasks
        taskList = loadTasks();

        // Create adapter
        taskAdapter = new TaskAdapter(
                taskList,
                new TaskAdapter.OnTaskActionListener() {

                    @Override
                    public void onEdit(int position) {
                        showEditDialog(position);
                    }

                    @Override
                    public void onDelete(int position) {
                        deleteTask(position);
                    }

                    @Override
                    public void onTaskChecked() {
                        saveTasks();
                    }
                }
        );

        // Setup RecyclerView
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerView.setAdapter(taskAdapter);

        // Add button
        addButton.setOnClickListener(v -> addTask());
    }

    // -------------------------
    // ADD TASK
    // -------------------------

    private void addTask() {

        String taskText = taskInput
                .getText()
                .toString()
                .trim();

        if (taskText.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter a task",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Task task = new Task(taskText);

        taskList.add(task);

        taskAdapter.notifyItemInserted(
                taskList.size() - 1
        );

        saveTasks();

        taskInput.setText("");

        Toast.makeText(
                this,
                "Task added",
                Toast.LENGTH_SHORT
        ).show();
    }

    // -------------------------
    // EDIT TASK
    // -------------------------

    private void showEditDialog(int position) {

        EditText editText = new EditText(this);

        editText.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        );

        editText.setText(
                taskList.get(position).getTaskText()
        );

        editText.setSelection(
                editText.length()
        );

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Edit Task")
                .setView(editText)
                .setNegativeButton(
                        "CANCEL",
                        null
                )
                .setPositiveButton(
                        "SAVE",
                        null
                )
                .create();

        dialog.setOnShowListener(d -> {

            dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener(v -> {

                String updatedTask = editText
                        .getText()
                        .toString()
                        .trim();

                if (updatedTask.isEmpty()) {

                    editText.setError(
                            "Task cannot be empty"
                    );

                    return;
                }

                taskList
                        .get(position)
                        .setTaskText(updatedTask);

                taskAdapter.notifyItemChanged(position);

                saveTasks();

                dialog.dismiss();

                Toast.makeText(
                        this,
                        "Task updated",
                        Toast.LENGTH_SHORT
                ).show();
            });
        });

        dialog.show();
    }

    // -------------------------
    // DELETE TASK
    // -------------------------

    private void deleteTask(int position) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage(
                        "Are you sure you want to delete this task?"
                )
                .setNegativeButton(
                        "CANCEL",
                        null
                )
                .setPositiveButton(
                        "DELETE",
                        (dialog, which) -> {

                            taskList.remove(position);

                            taskAdapter.notifyItemRemoved(
                                    position
                            );

                            saveTasks();

                            Toast.makeText(
                                    this,
                                    "Task deleted",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                )
                .show();
    }

    // -------------------------
    // SAVE TASKS
    // -------------------------

    private void saveTasks() {

        JSONArray jsonArray = new JSONArray();

        for (Task task : taskList) {

            JSONObject jsonObject = new JSONObject();

            try {

                jsonObject.put(
                        "taskText",
                        task.getTaskText()
                );

                jsonObject.put(
                        "completed",
                        task.isCompleted()
                );

                jsonArray.put(jsonObject);

            } catch (JSONException e) {

                e.printStackTrace();
            }
        }

        sharedPreferences
                .edit()
                .putString(
                        TASKS_KEY,
                        jsonArray.toString()
                )
                .apply();
    }

    // -------------------------
    // LOAD TASKS
    // -------------------------

    private ArrayList<Task> loadTasks() {

        ArrayList<Task> tasks = new ArrayList<>();

        String savedTasks = sharedPreferences.getString(
                TASKS_KEY,
                "[]"
        );

        try {

            JSONArray jsonArray =
                    new JSONArray(savedTasks);

            for (int i = 0;
                 i < jsonArray.length();
                 i++) {

                JSONObject jsonObject =
                        jsonArray.getJSONObject(i);

                String taskText =
                        jsonObject.getString("taskText");

                boolean completed =
                        jsonObject.getBoolean("completed");

                tasks.add(
                        new Task(
                                taskText,
                                completed
                        )
                );
            }

        } catch (JSONException e) {

            e.printStackTrace();
        }

        return tasks;
    }
}