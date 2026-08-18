package com.example.fitnesstracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView tvSteps, tvCalories, tvDuration, tvWorkouts;
    TextView tvStepsGoal, tvCaloriesGoal;
    ProgressBar progressSteps, progressCalories;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        tvSteps = findViewById(R.id.tvSteps);
        tvCalories = findViewById(R.id.tvCalories);
        tvDuration = findViewById(R.id.tvDuration);
        tvWorkouts = findViewById(R.id.tvWorkouts);

        tvStepsGoal = findViewById(R.id.tvStepsGoal);
        tvCaloriesGoal = findViewById(R.id.tvCaloriesGoal);

        progressSteps = findViewById(R.id.progressSteps);
        progressCalories = findViewById(R.id.progressCalories);

        TextView btnAddActivity = findViewById(R.id.btnAddActivity);
        TextView btnHistory = findViewById(R.id.btnHistory);

        databaseReference =
                FirebaseDatabase.getInstance().getReference("fitness_activities");

        btnAddActivity.setOnClickListener(v -> {

            Intent intent = new Intent(MainActivity.this,
                    AddActivity.class);

            startActivity(intent);

        });

        btnHistory.setOnClickListener(v -> {

            Intent intent = new Intent(MainActivity.this,
                    HistoryActivity.class);

            startActivity(intent);

        });

        loadTodayData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseReference != null) {
            loadTodayData();
        }
    }

    private void loadTodayData() {

        String today = new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(new Date());

        databaseReference
                .orderByChild("date")
                .equalTo(today)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot snapshot) {

                                int totalSteps = 0;
                                int totalCalories = 0;
                                int totalDuration = 0;
                                int totalWorkouts = 0;

                                for (DataSnapshot data :
                                        snapshot.getChildren()) {

                                    Long steps =
                                            data.child("steps")
                                                    .getValue(Long.class);

                                    Long calories =
                                            data.child("calories")
                                                    .getValue(Long.class);

                                    Long duration =
                                            data.child("duration")
                                                    .getValue(Long.class);

                                    if (steps != null) {
                                        totalSteps += steps.intValue();
                                    }

                                    if (calories != null) {
                                        totalCalories += calories.intValue();
                                    }

                                    if (duration != null) {
                                        totalDuration += duration.intValue();
                                    }

                                    totalWorkouts++;
                                }

                                tvSteps.setText(
                                        String.valueOf(totalSteps)
                                );

                                tvCalories.setText(
                                        String.valueOf(totalCalories)
                                );

                                tvDuration.setText(
                                        String.valueOf(totalDuration)
                                );

                                tvWorkouts.setText(
                                        String.valueOf(totalWorkouts)
                                );

                                progressSteps.setProgress(
                                        Math.min(totalSteps, 10000)
                                );

                                progressCalories.setProgress(
                                        Math.min(totalCalories, 600)
                                );

                                tvStepsGoal.setText(
                                        totalSteps + " / 10,000"
                                );

                                tvCaloriesGoal.setText(
                                        totalCalories + " / 600"
                                );
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {

                            }
                        });
    }
}