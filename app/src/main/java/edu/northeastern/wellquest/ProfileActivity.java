package edu.northeastern.wellquest;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
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
        barChart.getAxisRight().setEnabled(false);
        barChart.getXAxis().setTextColor(Color.WHITE);
        barChart.getLegend().setTextColor(Color.WHITE);

        // Dummy data for now, would typically come from history nodes in Firebase
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 1200));
        entries.add(new BarEntry(1, 4500));
        entries.add(new BarEntry(2, 3300));
        entries.add(new BarEntry(3, 8000));
        entries.add(new BarEntry(4, 5000));
        entries.add(new BarEntry(5, 7200));
        entries.add(new BarEntry(6, 6000));

        BarDataSet dataSet = new BarDataSet(entries, "Daily Steps");
        dataSet.setColor(Color.parseColor("#4CAF50"));
        dataSet.setValueTextColor(Color.WHITE);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.invalidate();
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
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }
}