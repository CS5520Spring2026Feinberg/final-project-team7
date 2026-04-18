package edu.northeastern.wellquest;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.northeastern.wellquest.helpers.GameHelper;
import edu.northeastern.wellquest.models.Player;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername, tvLevel, tvTotalSteps, tvStreak;
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvUsername = findViewById(R.id.tvUsername);
        tvLevel = findViewById(R.id.tvLevel);
        tvTotalSteps = findViewById(R.id.tvTotalSteps);
        tvStreak = findViewById(R.id.tvStreak);
        barChart = findViewById(R.id.barChart);

        setupChart();
        loadProfileData();
    }

    private void setupChart() {
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.getAxisLeft().setTextColor(Color.WHITE);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setTextColor(Color.WHITE);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        barChart.getLegend().setTextColor(Color.WHITE);
        barChart.setNoDataText("Tracking your epic journey...");
        barChart.setNoDataTextColor(Color.GRAY);
    }

    private void loadProfileData() {
        if (GameHelper.getUserRef() != null) {
            GameHelper.getUserRef().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Player player = snapshot.getValue(Player.class);
                    if (player != null) {
                        tvUsername.setText(player.getUsername());
                        tvLevel.setText("Level " + player.getLevel() + " Hero");
                        tvTotalSteps.setText(String.valueOf(player.getTotalSteps()));
                        tvStreak.setText(String.valueOf(player.getStreak()));
                    }

                    // Load history for chart
                    DataSnapshot historySnapshot = snapshot.child("history");
                    updateChartWithFirebaseData(historySnapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }

    private void updateChartWithFirebaseData(DataSnapshot historySnapshot) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> dates = new ArrayList<>();

        // Use a TreeMap to sort by date string
        Map<String, Integer> sortedHistory = new TreeMap<>();

        for (DataSnapshot daySnapshot : historySnapshot.getChildren()) {
            String date = daySnapshot.getKey();
            Integer steps = daySnapshot.child("steps").getValue(Integer.class);
            if (steps != null) {
                sortedHistory.put(date, steps);
            }
        }

        int index = 0;
        // Take last 7 days
        List<String> keys = new ArrayList<>(sortedHistory.keySet());
        int start = Math.max(0, keys.size() - 7);

        for (int i = start; i < keys.size(); i++) {
            String date = keys.get(i);
            int steps = sortedHistory.get(date);
            entries.add(new BarEntry(index, steps));
            // Show only MM-DD
            String label = date.length() > 5 ? date.substring(5) : date;
            dates.add(label);
            index++;
        }

        if (entries.isEmpty()) return;

        BarDataSet dataSet = new BarDataSet(entries, "Daily Steps");
        dataSet.setColor(Color.parseColor("#4CAF50"));
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(dates));
        barChart.getXAxis().setLabelCount(dates.size());
        barChart.setData(barData);
        barChart.animateY(1000);
        barChart.invalidate();
    }
}