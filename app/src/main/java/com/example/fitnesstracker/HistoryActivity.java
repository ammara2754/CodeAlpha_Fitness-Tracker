package com.example.fitnesstracker;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HistoryActivity extends AppCompatActivity {

    LinearLayout historyContainer;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_history);

        historyContainer = findViewById(R.id.historyContainer);

        databaseReference = FirebaseDatabase
                .getInstance()
                .getReference("fitness_activities");

        loadHistory();
    }

    private void loadHistory() {

        databaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        historyContainer.removeAllViews();

                        // Check if there are no activities
                        if (!snapshot.exists() ||
                                !snapshot.hasChildren()) {

                            showEmptyMessage();
                            return;
                        }

                        for (DataSnapshot data :
                                snapshot.getChildren()) {

                            String exercise =
                                    data.child("exerciseType")
                                            .getValue(String.class);

                            Long duration =
                                    data.child("duration")
                                            .getValue(Long.class);

                            Long calories =
                                    data.child("calories")
                                            .getValue(Long.class);

                            Long steps =
                                    data.child("steps")
                                            .getValue(Long.class);

                            String date =
                                    data.child("date")
                                            .getValue(String.class);

                            // Prevent null values
                            if (exercise == null) {
                                exercise = "Unknown Exercise";
                            }

                            if (duration == null) {
                                duration = 0L;
                            }

                            if (calories == null) {
                                calories = 0L;
                            }

                            if (steps == null) {
                                steps = 0L;
                            }

                            if (date == null) {
                                date = "Unknown date";
                            }

                            createActivityCard(
                                    exercise,
                                    duration,
                                    calories,
                                    steps,
                                    date
                            );
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                        historyContainer.removeAllViews();

                        TextView errorView =
                                new TextView(
                                        HistoryActivity.this
                                );

                        errorView.setText(
                                "Unable to load activity history.\n\n" +
                                        error.getMessage()
                        );

                        errorView.setTextSize(15);
                        errorView.setTextColor(
                                Color.rgb(150, 50, 50)
                        );

                        errorView.setGravity(Gravity.CENTER);
                        errorView.setPadding(
                                20,
                                40,
                                20,
                                40
                        );

                        historyContainer.addView(errorView);
                    }
                });
    }

    private void createActivityCard(
            String exercise,
            Long duration,
            Long calories,
            Long steps,
            String date) {

        // Main card
        LinearLayout card =
                new LinearLayout(
                        HistoryActivity.this
                );

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                20,
                18,
                20,
                18
        );

        card.setBackgroundResource(
                R.drawable.history_card
        );

        // Card layout parameters
        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0,
                0,
                0,
                16
        );

        card.setLayoutParams(cardParams);

        // Exercise title
        TextView title =
                new TextView(
                        HistoryActivity.this
                );

        title.setText(
                "🏋  " + exercise
        );

        title.setTextSize(19);
        title.setTextColor(
                Color.rgb(23, 59, 43)
        );

        title.setTypeface(
                null,
                Typeface.BOLD
        );

        // Add title
        card.addView(title);

        // Divider
        TextView divider =
                new TextView(
                        HistoryActivity.this
                );

        divider.setBackgroundColor(
                Color.rgb(224, 235, 228)
        );

        LinearLayout.LayoutParams dividerParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                );

        dividerParams.setMargins(
                0,
                14,
                0,
                14
        );

        divider.setLayoutParams(
                dividerParams
        );

        card.addView(divider);

        // Duration
        TextView durationView =
                createInfoText(
                        "⏱  Duration",
                        duration + " minutes"
                );

        card.addView(durationView);

        // Calories
        TextView caloriesView =
                createInfoText(
                        "🔥  Calories",
                        calories + " kcal"
                );

        card.addView(caloriesView);

        // Steps
        TextView stepsView =
                createInfoText(
                        "👟  Steps",
                        String.valueOf(steps)
                );

        card.addView(stepsView);

        // Date
        TextView dateView =
                createInfoText(
                        "📅  Date",
                        date
                );

        LinearLayout.LayoutParams dateParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        dateParams.setMargins(
                0,
                0,
                0,
                0
        );

        dateView.setLayoutParams(dateParams);

        card.addView(dateView);

        // Add card to container
        historyContainer.addView(card);
    }

    private TextView createInfoText(
            String label,
            String value) {

        TextView textView =
                new TextView(
                        HistoryActivity.this
                );

        textView.setText(
                label + "   " + value
        );

        textView.setTextSize(14);
        textView.setTextColor(
                Color.rgb(83, 101, 92)
        );

        textView.setPadding(
                0,
                6,
                0,
                6
        );

        return textView;
    }

    private void showEmptyMessage() {

        TextView emptyView =
                new TextView(
                        HistoryActivity.this
                );

        emptyView.setText(
                "📋\n\n" +
                        "No activities yet\n\n" +
                        "Start your first workout and\n" +
                        "your activity will appear here."
        );

        emptyView.setTextSize(16);
        emptyView.setTextColor(
                Color.rgb(113, 128, 120)
        );

        emptyView.setGravity(
                Gravity.CENTER
        );

        emptyView.setPadding(
                30,
                60,
                30,
                60
        );

        historyContainer.addView(
                emptyView
        );
    }
}