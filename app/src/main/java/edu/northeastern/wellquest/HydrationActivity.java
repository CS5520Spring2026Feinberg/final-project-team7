package edu.northeastern.wellquest;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import edu.northeastern.wellquest.helpers.GameHelper;
import edu.northeastern.wellquest.models.Player;

public class HydrationActivity extends AppCompatActivity {

    private TextView tvWaterCount, tvManaStatus;
    private MaterialButton btnAddWater;
    private LottieAnimationView lottieWater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hydration);

        tvWaterCount = findViewById(R.id.tvWaterCount);
        tvManaStatus = findViewById(R.id.tvManaStatus);
        btnAddWater = findViewById(R.id.btnAddWater);
        lottieWater = findViewById(R.id.lottieWater);

        loadPlayerData();

        btnAddWater.setOnClickListener(v -> {
            GameHelper.addWater(1);
            lottieWater.playAnimation();
        });
    }

    private void loadPlayerData() {
        if (GameHelper.getUserRef() != null) {
            GameHelper.getUserRef().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Player player = snapshot.getValue(Player.class);
                    if (player != null) {
                        tvWaterCount.setText(player.getWaterCups() + " / " + player.getWaterGoal() + " Cups");
                        tvManaStatus.setText("Current Mana: " + player.getMana() + " / " + player.getMaxMana());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }
}