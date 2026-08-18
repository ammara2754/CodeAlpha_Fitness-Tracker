package com.example.fitnesstracker;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import android.widget.ScrollView;
public class AddActivity extends AppCompatActivity {

    Spinner spinnerExercise;
    EditText etDuration;
    EditText etCalories;
    EditText etSteps;
    TextView btnSave;
    ScrollView addScrollView;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add);

        // Find views
        spinnerExercise = findViewById(R.id.spinnerExercise);
        etDuration = findViewById(R.id.etDuration);
        etCalories = findViewById(R.id.etCalories);
        etSteps = findViewById(R.id.etSteps);
        btnSave = findViewById(R.id.btnSave);
        addScrollView = findViewById(R.id.addScrollView);

        // Firebase Database reference
        databaseReference = FirebaseDatabase
                .getInstance()
                .getReference("fitness_activities");

        etSteps.setOnFocusChangeListener((v, hasFocus) -> {

            if (hasFocus) {
                addScrollView.postDelayed(() -> {
                    addScrollView.smoothScrollTo(
                            0,
                            etSteps.getBottom()
                    );
                }, 200);
            }
        });
        // Exercise list
        String[] exercises = {
                "Running",
                "Walking",
                "Cycling",
                "Gym",
                "Swimming",
                "Yoga",
                "Other"
        };

        // Spinner adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                exercises
        );

        spinnerExercise.setAdapter(adapter);

        // Save button
        btnSave.setOnClickListener(v -> saveActivity());
    }

    private void saveActivity() {

        // Get selected exercise
        String exercise = spinnerExercise
                .getSelectedItem()
                .toString();

        // Get entered values
        String durationText = etDuration
                .getText()
                .toString()
                .trim();

        String caloriesText = etCalories
                .getText()
                .toString()
                .trim();

        String stepsText = etSteps
                .getText()
                .toString()
                .trim();

        // Check empty fields
        if (TextUtils.isEmpty(durationText) ||
                TextUtils.isEmpty(caloriesText) ||
                TextUtils.isEmpty(stepsText)) {

            Toast.makeText(
                    AddActivity.this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Convert values to numbers
        int duration;
        int calories;
        int steps;

        try {

            duration = Integer.parseInt(durationText);
            calories = Integer.parseInt(caloriesText);
            steps = Integer.parseInt(stepsText);

        } catch (NumberFormatException e) {

            Toast.makeText(
                    AddActivity.this,
                    "Please enter valid numbers",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Check for negative values
        if (duration < 0 || calories < 0 || steps < 0) {

            Toast.makeText(
                    AddActivity.this,
                    "Values cannot be negative",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Get current date
        String date = new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(new Date());

        // Generate Firebase ID
        String activityId = databaseReference
                .push()
                .getKey();

        if (activityId == null) {

            Toast.makeText(
                    AddActivity.this,
                    "Could not create activity ID",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Create activity data
        Map<String, Object> activity = new HashMap<>();

        activity.put("exerciseType", exercise);
        activity.put("duration", duration);
        activity.put("calories", calories);
        activity.put("steps", steps);
        activity.put("date", date);

        // Change button text while saving
        btnSave.setText("SAVING...");
        btnSave.setClickable(false);

        databaseReference
                .child(activityId)
                .setValue(activity)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        Toast.makeText(
                                AddActivity.this,
                                "Activity saved successfully!",
                                Toast.LENGTH_SHORT
                        ).show();

                        finish();

                    } else {

                        btnSave.setClickable(true);
                        btnSave.setText("SAVE ACTIVITY   ✓");

                        String errorMessage = "Unknown Firebase error";

                        if (task.getException() != null) {
                            errorMessage = task.getException().getMessage();
                        }

                        Toast.makeText(
                                AddActivity.this,
                                "Firebase Error: " + errorMessage,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}