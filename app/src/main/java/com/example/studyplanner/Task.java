package com.example.studyplanner;

import java.io.Serializable;
import java.util.Date;

public class Task implements Serializable {
    private String taskName;
    private String description;
    private boolean isDone;
    private int pomodoroCount = 0;
    private Date dueDate;

    public Task(String taskName, String description) {
        this.taskName = taskName;
        this.description = description;
        this.isDone = false;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public int getPomodoroCount() {
        return pomodoroCount;
    }

    public void incrementPomodoroCount() {
        pomodoroCount++;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
}