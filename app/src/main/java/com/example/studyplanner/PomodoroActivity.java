package com.example.studyplanner;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class PomodoroActivity extends AppCompatActivity {

    private TextView timerText, quoteText, taskTitle, currentSoundText;
    private Button startButton, stopButton;
    private ProgressBar progressCircle;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private boolean isBreakTime = false;
    private long timeLeftInMillis;
    private long totalTimeInMillis;
    private Task currentTask;
    private Spinner focusDurationSpinner;
    private MediaPlayer mediaPlayer;
    private int currentSoundIndex = 0;
    private int[] soundResIds = {
            R.raw.lightrain,
            R.raw.sunnyday,
            R.raw.oceanwaves,
            R.raw.wind,
            R.raw.meditation
    };
    private String[] soundNames = {
            "Light Rain 🌧️",
            "Sunny Day ☀️",
            "Ocean Waves 🌊",
            "Wind 🍃",
            "Meditation 🧘‍♀️"
    };
    private ImageButton toggleSoundButton;
    private boolean isSoundMuted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);

        // Init views
        timerText = findViewById(R.id.timerText);
        quoteText = findViewById(R.id.quoteText);
        taskTitle = findViewById(R.id.taskTitle);
        progressCircle = findViewById(R.id.progressCircle);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        focusDurationSpinner = findViewById(R.id.focusDurationSpinner);
        currentSoundText = findViewById(R.id.currentSoundText);
        ImageButton toggleSoundButton = findViewById(R.id.toggleSoundButton); // NEW

        // Media player setup
        mediaPlayer = MediaPlayer.create(this, soundResIds[currentSoundIndex]);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        currentSoundText.setText("🎵 Current Sound: " + soundNames[currentSoundIndex]);

        currentTask = (Task) getIntent().getSerializableExtra("task");

        if (currentTask != null) {
            taskTitle.setText("Task: " + currentTask.getTaskName());
        }

        // Spinner
        String[] durations = {"🍅 25 min", "🚀 50 min", "🔥 90 min", "💯 120 min"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, durations);
        focusDurationSpinner.setAdapter(adapter);

        focusDurationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isTimerRunning && !isBreakTime) {
                    totalTimeInMillis = getInitialFocusTime();
                    timeLeftInMillis = totalTimeInMillis;
                    updateTimerText(timeLeftInMillis);
                    updateProgress();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        totalTimeInMillis = getInitialFocusTime();
        timeLeftInMillis = totalTimeInMillis;

        updateTimerText(timeLeftInMillis);
        updateProgress();

        startButton.setOnClickListener(v -> {
            if (!isTimerRunning) {
                if (!isBreakTime) {
                    totalTimeInMillis = getInitialFocusTime();
                    timeLeftInMillis = totalTimeInMillis;
                    updateTimerText(timeLeftInMillis);
                    updateProgress();
                }
                startTimer();
                Toast.makeText(this, "🎯 Focus mode started!", Toast.LENGTH_SHORT).show();
            }
        });

        stopButton.setOnClickListener(v -> {
            pauseTimer();
            isBreakTime = false;
            totalTimeInMillis = getInitialFocusTime();
            timeLeftInMillis = totalTimeInMillis;
            updateTimerText(timeLeftInMillis);
            updateProgress();
            Toast.makeText(this, "⏹️ Stopped and reset to focus", Toast.LENGTH_SHORT).show();
        });

        ImageButton switchSoundButton = findViewById(R.id.switchSoundButton);
        switchSoundButton.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }

            currentSoundIndex = (currentSoundIndex + 1) % soundResIds.length;

            mediaPlayer = MediaPlayer.create(this, soundResIds[currentSoundIndex]);
            mediaPlayer.setLooping(true);
            if (!isSoundMuted) mediaPlayer.start();

            currentSoundText.setText("🎵 Current Sound: " + soundNames[currentSoundIndex]);
        });

        toggleSoundButton.setOnClickListener(v -> {
            isSoundMuted = !isSoundMuted;
            if (isSoundMuted) {
                if (mediaPlayer != null) mediaPlayer.setVolume(0f, 0f);
                toggleSoundButton.setImageResource(R.drawable.ic_headphones_muted);
            } else {
                if (mediaPlayer != null) mediaPlayer.setVolume(1f, 1f);
                toggleSoundButton.setImageResource(R.drawable.ic_headphones);
            }
        });
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText(timeLeftInMillis);
                updateProgress();
            }

            public void onFinish() {
                isTimerRunning = false;

                if (!isBreakTime) {
                    isBreakTime = true;

                    if (currentTask != null) {
                        currentTask.incrementPomodoroCount();
                        Toast.makeText(PomodoroActivity.this,
                                "🎉 Focus done! Added 1 Pomodoro to: " + currentTask.getTaskName(),
                                Toast.LENGTH_SHORT).show();
                    }

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("focusedTask", currentTask);
                    setResult(RESULT_OK, resultIntent);

                    totalTimeInMillis = getBreakTime();
                    timeLeftInMillis = totalTimeInMillis;
                    startTimer();
                } else {
                    isBreakTime = false;
                    Toast.makeText(PomodoroActivity.this, "🔔 Break over! Ready for next session?", Toast.LENGTH_SHORT).show();
                    resetTimerView();
                }
            }
        }.start();

        isTimerRunning = true;
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isTimerRunning = false;
    }

    private void updateTimerText(long millis) {
        int minutes = (int) (millis / 1000) / 60;
        int seconds = (int) (millis / 1000) % 60;
        String timeFormatted = String.format("%02d:%02d", minutes, seconds);

        if (millis == 0) {
            timerText.setText("Ready");
        } else {
            timerText.setText(timeFormatted);
        }

        quoteText.setText(isBreakTime ? "Take a deep breath ☁️" : "You’ve got this 💪");
    }

    private void updateProgress() {
        int progress = (int) ((double) timeLeftInMillis / totalTimeInMillis * 100);
        progressCircle.setProgress(progress);
    }

    private void resetTimerView() {
        pauseTimer();
        totalTimeInMillis = getInitialFocusTime();
        timeLeftInMillis = totalTimeInMillis;
        updateTimerText(timeLeftInMillis);
        updateProgress();
    }

    private long getInitialFocusTime() {
        if (focusDurationSpinner == null) return 25 * 60 * 1000;

        String selected = focusDurationSpinner.getSelectedItem().toString();

        if (selected.contains("50")) return 50 * 60 * 1000;
        else if (selected.contains("90")) return 90 * 60 * 1000;
        else if (selected.contains("120")) return 120 * 60 * 1000;
        else return 25 * 60 * 1000;
    }

    private long getBreakTime() {
        return 5 * 60 * 1000;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}