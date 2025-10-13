package com.example.studyplanner;

import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Calendar;


public class CalendarActivity extends AppCompatActivity {

    private List<Task> taskList = MainActivity.masterTaskList;
    private List<Task> filteredList = new ArrayList<>();
    private RecyclerView calendarTaskList;
    private TaskAdapter adapter;
    private TextView selectedDateText;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarTaskList = findViewById(R.id.calendarTaskList);
        selectedDateText = findViewById(R.id.selectedDateText);

        calendarTaskList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(this, filteredList, new TaskAdapter.TaskClickListener() {
            @Override public void onEditTask(Task task) {}
            @Override public void onDeleteTask(Task task) {}
            @Override public void onToggleTaskDone(Task task) {}
            @Override public void onFocusTask(Task task) {}
        });
        calendarTaskList.setAdapter(adapter);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selectedCal = Calendar.getInstance();
            selectedCal.set(year, month, dayOfMonth);
            Date selectedDate = selectedCal.getTime();

            selectedDateText.setText("Tasks for: " + new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(selectedDate));

            filteredList.clear();
            String selectedDateKey = dateFormatter.format(selectedDate);
            for (Task task : taskList) {
                if (task.getDueDate() != null && dateFormatter.format(task.getDueDate()).equals(selectedDateKey)) {
                    filteredList.add(task);
                }
            }
            adapter.notifyDataSetChanged();
        });
    }
}
