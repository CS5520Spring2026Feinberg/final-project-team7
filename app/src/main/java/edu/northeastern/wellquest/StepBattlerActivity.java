package edu.northeastern.wellquest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.northeastern.wellquest.helpers.GameHelper;

public class StepBattlerActivity extends AppCompatActivity implements SensorEventListener {

    private TextView tvMonsterName, tvMonsterHealth, tvDamageDealt, tvStepCount, tvXpReward;
    private ProgressBar pbMonsterHealth;
    private Button btnNewMonster;
    private LottieAnimationView lottieMonster;

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private boolean sensorRegistered = false;

    private int initialStepCount = -1;
    private int sessionSteps = 0;

    private String monsterName = "Slime";
    private int monsterHealth = 100;
    private int monsterMaxHealth = 100;
    private int monsterXpReward = 20;
    private int totalDamageDealt = 0;

    private DatabaseReference mUserRef;

    private static final String KEY_INITIAL_STEPS = "initial_steps";
    private static final String KEY_SESSION_STEPS = "session_steps";
    private static final String KEY_MONSTER_NAME = "monster_name";
    private static final String KEY_MONSTER_HEALTH = "monster_health";
    private static final String KEY_MONSTER_MAX_HEALTH = "monster_max_health";
    private static final String KEY_MONSTER_XP = "monster_xp";
    private static final String KEY_TOTAL_DAMAGE = "total_damage";

    private final String[][] monsters = {
            {"Slime", "100", "20"},
            {"Goblin", "200", "40"},
            {"Skeleton", "350", "60"},
            {"Dark Wolf", "500", "80"},
            {"Dragon Whelp", "750", "120"},
            {"Fire Golem", "1000", "150"}
    };

    private int currentMonsterIndex = 0;

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    registerStepSensor();
                } else {
                    Toast.makeText(this, "Activity recognition permission is needed for step counting", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_battler);

        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (uid != null) {
            mUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        }

        tvMonsterName = findViewById(R.id.tv_monster_name);
        tvMonsterHealth = findViewById(R.id.tv_monster_health);
        tvDamageDealt = findViewById(R.id.tv_damage_dealt);
        tvStepCount = findViewById(R.id.tv_battle_steps);
        tvXpReward = findViewById(R.id.tv_xp_reward);
        pbMonsterHealth = findViewById(R.id.pb_monster_health);
        btnNewMonster = findViewById(R.id.btn_new_monster);
        lottieMonster = findViewById(R.id.lottieMonster);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (savedInstanceState != null) {
            initialStepCount = savedInstanceState.getInt(KEY_INITIAL_STEPS, -1);
            sessionSteps = savedInstanceState.getInt(KEY_SESSION_STEPS, 0);
            monsterName = savedInstanceState.getString(KEY_MONSTER_NAME, "Slime");
            monsterHealth = savedInstanceState.getInt(KEY_MONSTER_HEALTH, 100);
            monsterMaxHealth = savedInstanceState.getInt(KEY_MONSTER_MAX_HEALTH, 100);
            monsterXpReward = savedInstanceState.getInt(KEY_MONSTER_XP, 20);
            totalDamageDealt = savedInstanceState.getInt(KEY_TOTAL_DAMAGE, 0);
        }

        updateMonsterUI();
        updateStepUI();

        btnNewMonster.setOnClickListener(v -> spawnNextMonster());

        if (stepSensor == null) {
            Toast.makeText(this, "Step counter sensor not available on this device", Toast.LENGTH_LONG).show();
        } else {
            checkAndRequestPermission();
        }
    }

    private void checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
                registerStepSensor();
            } else {
                permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION);
            }
        } else {
            registerStepSensor();
        }
    }

    private void registerStepSensor() {
        if (!sensorRegistered && stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
            sensorRegistered = true;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int totalSteps = (int) event.values[0];

            if (initialStepCount == -1) {
                initialStepCount = totalSteps;
            }

            int newSessionSteps = totalSteps - initialStepCount;
            int stepsSinceLastUpdate = newSessionSteps - sessionSteps;
            sessionSteps = newSessionSteps;

            if (stepsSinceLastUpdate > 0 && monsterHealth > 0) {
                int damage = GameHelper.calculateDamage(stepsSinceLastUpdate * 10);
                if (damage < 1) damage = 1;
                totalDamageDealt += damage;
                monsterHealth = Math.max(0, monsterHealth - damage);

                updateMonsterUI();
                updateStepUI();

                // Play "damage" effect
                lottieMonster.playAnimation();

                GameHelper.addSteps(stepsSinceLastUpdate);

                if (monsterHealth <= 0) {
                    onMonsterDefeated();
                }
            } else {
                updateStepUI();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void onMonsterDefeated() {
        GameHelper.addXp(monsterXpReward);
        tvMonsterName.setText(monsterName + " DEFEATED!");
        tvDamageDealt.setText("Victory! +" + monsterXpReward + " XP");
        btnNewMonster.setEnabled(true);
        lottieMonster.pauseAnimation();

        if (mUserRef != null) {
            mUserRef.child("monstersDefeated").get().addOnSuccessListener(snapshot -> {
                int defeated = snapshot.getValue(Integer.class) != null ? snapshot.getValue(Integer.class) : 0;
                mUserRef.child("monstersDefeated").setValue(defeated + 1);
            });
        }
    }

    private void spawnNextMonster() {
        currentMonsterIndex = (currentMonsterIndex + 1) % monsters.length;
        monsterName = monsters[currentMonsterIndex][0];
        monsterMaxHealth = Integer.parseInt(monsters[currentMonsterIndex][1]);
        monsterHealth = monsterMaxHealth;
        monsterXpReward = Integer.parseInt(monsters[currentMonsterIndex][2]);
        totalDamageDealt = 0;
        btnNewMonster.setEnabled(false);
        updateMonsterUI();
        lottieMonster.resumeAnimation();
    }

    private void updateMonsterUI() {
        tvMonsterName.setText(monsterName);
        tvMonsterHealth.setText(String.format("HP: %d / %d", monsterHealth, monsterMaxHealth));
        pbMonsterHealth.setMax(monsterMaxHealth);
        pbMonsterHealth.setProgress(monsterHealth);
        tvDamageDealt.setText(String.format("Damage Dealt: %d", totalDamageDealt));
        tvXpReward.setText(String.format("XP Reward: %d", monsterXpReward));

        if (monsterHealth <= 0) {
            btnNewMonster.setEnabled(true);
        }
    }

    private void updateStepUI() {
        tvStepCount.setText(String.format("Battle Steps: %d", sessionSteps));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_INITIAL_STEPS, initialStepCount);
        outState.putInt(KEY_SESSION_STEPS, sessionSteps);
        outState.putString(KEY_MONSTER_NAME, monsterName);
        outState.putInt(KEY_MONSTER_HEALTH, monsterHealth);
        outState.putInt(KEY_MONSTER_MAX_HEALTH, monsterMaxHealth);
        outState.putInt(KEY_MONSTER_XP, monsterXpReward);
        outState.putInt(KEY_TOTAL_DAMAGE, totalDamageDealt);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorRegistered || stepSensor == null) return;
        checkAndRequestPermission();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorRegistered) {
            sensorManager.unregisterListener(this);
            sensorRegistered = false;
        }
    }
}