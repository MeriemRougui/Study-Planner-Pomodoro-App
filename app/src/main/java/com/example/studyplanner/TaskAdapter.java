package com.example.studyplanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private TaskClickListener listener;
    private Context context;

    public TaskAdapter(Context context, List<Task> taskList, TaskClickListener listener) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskName.setText(task.getTaskName());
        holder.taskDescription.setText(task.getDescription());
        holder.pomodoroText.setText("🎯 Focus Sessions: " + task.getPomodoroCount());

        if (task.getDueDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            String formattedDate = sdf.format(task.getDueDate());
            holder.dueDateText.setText("Due: " + formattedDate);
        } else {
            holder.dueDateText.setText("No due date");
        }

        holder.editButton.setOnClickListener(v -> listener.onEditTask(task));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteTask(task));
        holder.toggleDoneButton.setText(task.isDone() ? "Undo" : "Done");
        holder.toggleDoneButton.setOnClickListener(v -> listener.onToggleTaskDone(task));

        holder.focusButton.setOnClickListener(view -> {
            listener.onFocusTask(task);
            notifyItemChanged(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void updateTasks(List<Task> updatedList) {
        this.taskList = updatedList;
        notifyDataSetChanged();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskName, taskDescription, pomodoroText, dueDateText;
        Button editButton, deleteButton, toggleDoneButton, focusButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.taskName);
            taskDescription = itemView.findViewById(R.id.taskDescription);
            pomodoroText = itemView.findViewById(R.id.pomodoroText);
            dueDateText = itemView.findViewById(R.id.dueDateText);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            toggleDoneButton = itemView.findViewById(R.id.toggleDoneButton);
            focusButton = itemView.findViewById(R.id.focusButton);
        }
    }

    public interface TaskClickListener {
        void onEditTask(Task task);
        void onDeleteTask(Task task);
        void onToggleTaskDone(Task task);
        void onFocusTask(Task task);
    }
}