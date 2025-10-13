package com.example.studyplanner;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        EditText taskNameEditText = findViewById(R.id.taskNameEditText);
        EditText taskDescriptionEditText = findViewById(R.id.taskDescriptionEditText);
        TextView dueDateText = findViewById(R.id.dueDateText);
        Button pickDueDateButton = findViewById(R.id.pickDueDateButton);
        Button addTaskButton = findViewById(R.id.addTaskButton);

        final Calendar calendar = Calendar.getInstance();
        final Date[] selectedDueDate = {null};

        pickDueDateButton.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year1, month1, dayOfMonth) -> {
                        calendar.set(year1, month1, dayOfMonth);
                        selectedDueDate[0] = calendar.getTime();

                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                        dueDateText.setText("Due: " + sdf.format(selectedDueDate[0]));
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        addTaskButton.setOnClickListener(v -> {
            String taskName = taskNameEditText.getText().toString().trim();
            String taskDescription = taskDescriptionEditText.getText().toString().trim();

            if (!taskName.isEmpty() && !taskDescription.isEmpty()) {
                Task newTask = new Task(taskName, taskDescription);
                if (selectedDueDate[0] != null) {
                    newTask.setDueDate(selectedDueDate[0]);
                }

                Intent resultIntent = new Intent();
                resultIntent.putExtra("task", newTask);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}