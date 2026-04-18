package edu.northeastern.wellquest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvUsername, tvLevel, tvXp, tvHealth, tvMana, tvSteps, tvStreak;
    private ProgressBar pbXp, pbHealth, pbMana;
    private Button btnStepBattler, btnHydration, btnGuild, btnProfile, btnAbout, btnLogout;
    private LottieAnimationView lottieAvatar;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        mUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());

        tvUsername = findViewById(R.id.tv_dashboard_username);
        tvLevel = findViewById(R.id.tv_dashboard_level);
        tvXp = findViewById(R.id.tv_dashboard_xp);
        tvHealth = findViewById(R.id.tv_dashboard_health);
        tvMana = findViewById(R.id.tv_dashboard_mana);
        tvSteps = findViewById(R.id.tv_dashboard_steps);
        tvStreak = findViewById(R.id.tv_dashboard_streak);
        pbXp = findViewById(R.id.pb_xp);
        pbHealth = findViewById(R.id.pb_health);
        pbMana = findViewById(R.id.pb_mana);
        btnStepBattler = findViewById(R.id.btn_step_battler);
        btnHydration = findViewById(R.id.btn_hydration);
        btnGuild = findViewById(R.id.btn_guild);
        btnProfile = findViewById(R.id.btn_profile);
        btnAbout = findViewById(R.id.btn_about);
        btnLogout = findViewById(R.id.btn_logout);
        lottieAvatar = findViewById(R.id.lottie_avatar);

        loadUserData();

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        btnAbout.setOnClickListener(v -> {
            startActivity(new Intent(this, AboutActivity.class));
        });

        btnStepBattler.setOnClickListener(v -> {
            startActivity(new Intent(this, StepBattlerActivity.class));
        });

        btnHydration.setOnClickListener(v -> {
            startActivity(new Intent(this, HydrationActivity.class));
        });

        btnGuild.setOnClickListener(v -> {
            Toast.makeText(this, "Guild coming soon!", Toast.LENGTH_SHORT).show();
        });

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });
    }

    private void loadUserData() {
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;

                String username = snapshot.child("username").getValue(String.class);
                int level = snapshot.child("level").getValue(Integer.class) != null ? snapshot.child("level").getValue(Integer.class) : 1;
                int xp = snapshot.child("xp").getValue(Integer.class) != null ? snapshot.child("xp").getValue(Integer.class) : 0;
                int health = snapshot.child("health").getValue(Integer.class) != null ? snapshot.child("health").getValue(Integer.class) : 100;
                int maxHealth = snapshot.child("maxHealth").getValue(Integer.class) != null ? snapshot.child("maxHealth").getValue(Integer.class) : 100;
                int mana = snapshot.child("mana").getValue(Integer.class) != null ? snapshot.child("mana").getValue(Integer.class) : 50;
                int maxMana = snapshot.child("maxMana").getValue(Integer.class) != null ? snapshot.child("maxMana").getValue(Integer.class) : 50;
                int stepsToday = snapshot.child("stepsToday").getValue(Integer.class) != null ? snapshot.child("stepsToday").getValue(Integer.class) : 0;
                int streak = snapshot.child("streak").getValue(Integer.class) != null ? snapshot.child("streak").getValue(Integer.class) : 0;

                int xpForNextLevel = level * 100;

                tvUsername.setText(username != null ? username : "Hero");

                // Dynamic Level Title
                String title = "Warrior";
                if (level >= 10) title = "Paladin";
                else if (level >= 5) title = "Knight";

                tvLevel.setText(String.format("Level %d %s", level, title));
                tvXp.setText(String.format(getString(R.string.xp_format), xp, xpForNextLevel));
                tvHealth.setText(String.format(getString(R.string.health_format), health, maxHealth));
                tvMana.setText(String.format(getString(R.string.mana_format), mana, maxMana));
                tvSteps.setText(String.format(getString(R.string.steps_today), stepsToday));
                tvStreak.setText(String.format(getString(R.string.streak_format), streak));

                pbXp.setMax(xpForNextLevel);
                pbXp.setProgress(xp);
                pbHealth.setMax(maxHealth);
                pbHealth.setProgress(health);
                pbMana.setMax(maxMana);
                pbMana.setProgress(mana);

                // Update Avatar based on level (Visual Evolution)
                updateAvatar(level);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAvatar(int level) {
        // Logic to switch between different avatar animations as the player levels up
        // These raw resources should be added to the project
        if (level >= 10) {
            // lottieAvatar.setAnimation(R.raw.hero_paladin);
        } else if (level >= 5) {
            // lottieAvatar.setAnimation(R.raw.hero_knight);
        } else {
            // lottieAvatar.setAnimation(R.raw.hero_warrior);
        }
        lottieAvatar.playAnimation();
    }
}