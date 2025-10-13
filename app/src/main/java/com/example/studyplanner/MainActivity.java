package com.example.studyplanner;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public static List<Task> masterTaskList = new ArrayList<>();
    private List<Task> taskList = new ArrayList<>();
    private TaskAdapter taskAdapter;
    private boolean showingDoneTasks = false;
    private Button toggleDoneTasksButton;
    private RecyclerView recyclerView;
    private TextView statsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String username = getIntent().getStringExtra("username");

        if (username != null) {
            Toast.makeText(this, "Welcome, " + username + "!", Toast.LENGTH_LONG).show();
        }

        TextView motivationTextView = findViewById(R.id.motivationTextView);
        String[] motivationalQuotes = {
                "You've got this! 💪",
                "Small steps every day 💖",
                "Focus and finish strong 🌟",
                "Keep going, future CEO! 🚀",
                "One task at a time 🧘‍♀️"
        };
        motivationTextView.setText(motivationalQuotes[new Random().nextInt(motivationalQuotes.length)]);

        statsTextView = findViewById(R.id.statsTextView);
        recyclerView = findViewById(R.id.taskRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        taskAdapter = new TaskAdapter(MainActivity.this, taskList, new TaskAdapter.TaskClickListener() {
            @Override
            public void onEditTask(Task task) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Edit Task");

                LinearLayout layout = new LinearLayout(MainActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setPadding(50, 40, 50, 10);

                final EditText nameInput = new EditText(MainActivity.this);
                nameInput.setHint("Task Name");
                nameInput.setText(task.getTaskName());
                layout.addView(nameInput);

                final EditText descInput = new EditText(MainActivity.this);
                descInput.setHint("Task Description");
                descInput.setText(task.getDescription());
                layout.addView(descInput);

                builder.setView(layout);
                builder.setPositiveButton("Save", (dialog, which) -> {
                    String newName = nameInput.getText().toString().trim();
                    String newDesc = descInput.getText().toString().trim();
                    if (!newName.isEmpty() && !newDesc.isEmpty()) {
                        task.setTaskName(newName);
                        task.setDescription(newDesc);
                        taskAdapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "Task Updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Both fields required!", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                builder.show();
            }

            @Override
            public void onDeleteTask(Task task) {
                taskList.remove(task);
                masterTaskList.remove(task);
                filterTasks();
            }

            @Override
            public void onToggleTaskDone(Task task) {
                task.setDone(!task.isDone());
                filterTasks();
            }

            @Override
            public void onFocusTask(Task task) {
                Intent intent = new Intent(MainActivity.this, PomodoroActivity.class);
                intent.putExtra("focusedTask", task);
                startActivityForResult(intent, 2);
            }
        });

        recyclerView.setAdapter(taskAdapter);

        Button addTaskButton = findViewById(R.id.addTaskButton);
        addTaskButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivityForResult(intent, 1);
        });

        Button pomodoroButton = findViewById(R.id.pomodoroButton);
        pomodoroButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PomodoroActivity.class);
            startActivity(intent);
        });

        Button calendarButton = findViewById(R.id.calendarButton);
        calendarButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
            startActivity(intent);
        });

        toggleDoneTasksButton = findViewById(R.id.toggleDoneTasksButton);
        toggleDoneTasksButton.setOnClickListener(v -> {
            showingDoneTasks = !showingDoneTasks;
            filterTasks();
            toggleDoneTasksButton.setText(showingDoneTasks ? "Show Done Tasks" : "Show Undone Tasks");
        });
    }

    private void filterTasks() {
        List<Task> filtered = new ArrayList<>();
        int completed = 0;
        for (Task task : taskList) {
            if (task.isDone()) completed++;
            if (showingDoneTasks && task.isDone()) {
                filtered.add(task);
            } else if (!showingDoneTasks && !task.isDone()) {
                filtered.add(task);
            }
        }

        statsTextView.setText("Daily Progress: " + completed + " completed out of " + taskList.size());
        taskAdapter.updateTasks(filtered);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Task newTask = (Task) data.getSerializableExtra("task");

            if (newTask != null) {
                taskList.add(newTask);
                MainActivity.masterTaskList.add(newTask);
                filterTasks();
                Toast.makeText(this, "Task Added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Invalid task data", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Task updatedTask = (Task) data.getSerializableExtra("focusedTask");

            for (Task task : taskList) {
                if (task.getTaskName().equals(updatedTask.getTaskName()) &&
                        task.getDescription().equals(updatedTask.getDescription())) {
                    task.incrementPomodoroCount();
                    break;
                }
            }

            filterTasks();
        }
    }
}