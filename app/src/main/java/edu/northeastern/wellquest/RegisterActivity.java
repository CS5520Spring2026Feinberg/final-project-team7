package edu.northeastern.wellquest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private EditText etUsername, etEmail, etPassword;
    private Button btnRegister;
    private ProgressBar pbRegister;
    private TextView tvGoToLogin;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        etUsername = findViewById(R.id.et_register_username);
        etEmail = findViewById(R.id.et_register_email);
        etPassword = findViewById(R.id.et_register_password);
        btnRegister = findViewById(R.id.btn_register);
        pbRegister = findViewById(R.id.pb_register);
        tvGoToLogin = findViewById(R.id.tv_go_to_login);

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "Starting registration process for: " + username);
            setLoading(true);
            checkUsernameAndRegister(username, email, password);
        });

        tvGoToLogin.setOnClickListener(v -> finish());
    }

    private void setLoading(boolean isLoading) {
        btnRegister.setEnabled(!isLoading);
        pbRegister.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void checkUsernameAndRegister(String username, String email, String password) {
        Log.d(TAG, "Checking username availability...");
        mDatabase.child("usernames").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.w(TAG, "Username already taken: " + username);
                    setLoading(false);
                    Toast.makeText(RegisterActivity.this, "Username already taken", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Username available, creating account...");
                    createAccount(username, email, password);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Username check cancelled: " + error.getMessage());
                setLoading(false);
                Toast.makeText(RegisterActivity.this, "Error checking username: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createAccount(String username, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "FirebaseAuth account created successfully");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserToDatabase(user.getUid(), username, email);
                        } else {
                            Log.e(TAG, "User is null after successful creation!");
                            setLoading(false);
                            Toast.makeText(this, "Critical Error: User creation failed.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "FirebaseAuth creation failed", task.getException());
                        setLoading(false);
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(this, "Registration failed: " + error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserToDatabase(String uid, String username, String email) {
        Log.d(TAG, "Saving user data to Realtime Database...");
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("email", email);
        userData.put("level", 1);
        userData.put("xp", 0);
        userData.put("health", 100);
        userData.put("maxHealth", 100);
        userData.put("mana", 50);
        userData.put("maxMana", 50);
        userData.put("stepsToday", 0);
        userData.put("totalSteps", 0);
        userData.put("streak", 0);
        userData.put("waterCups", 0);
        userData.put("waterGoal", 8);
        userData.put("guildId", "");

        Map<String, Object> updates = new HashMap<>();
        updates.put("users/" + uid, userData);
        updates.put("usernames/" + username, uid);

        mDatabase.updateChildren(updates).addOnCompleteListener(task -> {
            setLoading(false);
            if (task.isSuccessful()) {
                Log.d(TAG, "User data saved successfully. Registration complete.");
                Toast.makeText(this, "Account created! Welcome, " + username, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Log.e(TAG, "Database update failed", task.getException());
                String error = task.getException() != null ? task.getException().getMessage() : "Permission denied";
                Toast.makeText(this, "Failed to save user data: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}