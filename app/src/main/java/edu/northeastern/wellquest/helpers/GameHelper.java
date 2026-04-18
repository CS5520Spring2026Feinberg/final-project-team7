package edu.northeastern.wellquest.helpers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GameHelper {

    private static final DatabaseReference DB = FirebaseDatabase.getInstance().getReference();

    public static String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
    }

    public static DatabaseReference getUserRef() {
        String uid = getCurrentUserId();
        if (uid == null) return null;
        return DB.child("users").child(uid);
    }

    public static void addXp(int amount) {
        DatabaseReference userRef = getUserRef();
        if (userRef == null) return;

        userRef.get().addOnSuccessListener(snapshot -> {
            int currentXp = snapshot.child("xp").getValue(Integer.class) != null ? snapshot.child("xp").getValue(Integer.class) : 0;
            int currentLevel = snapshot.child("level").getValue(Integer.class) != null ? snapshot.child("level").getValue(Integer.class) : 1;
            int maxHealth = snapshot.child("maxHealth").getValue(Integer.class) != null ? snapshot.child("maxHealth").getValue(Integer.class) : 100;
            int maxMana = snapshot.child("maxMana").getValue(Integer.class) != null ? snapshot.child("maxMana").getValue(Integer.class) : 50;

            int newXp = currentXp + amount;
            int xpForNextLevel = currentLevel * 100;

            Map<String, Object> updates = new HashMap<>();

            if (newXp >= xpForNextLevel) {
                int newLevel = currentLevel + 1;
                int leftoverXp = newXp - xpForNextLevel;
                int newMaxHealth = 100 + (newLevel - 1) * 10;
                int newMaxMana = 50 + (newLevel - 1) * 5;

                updates.put("level", newLevel);
                updates.put("xp", leftoverXp);
                updates.put("maxHealth", newMaxHealth);
                updates.put("health", newMaxHealth);
                updates.put("maxMana", newMaxMana);
                updates.put("mana", newMaxMana);
            } else {
                updates.put("xp", newXp);
            }

            userRef.updateChildren(updates);
        });
    }

    public static void addSteps(int steps) {
        DatabaseReference userRef = getUserRef();
        if (userRef == null) return;

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        userRef.get().addOnSuccessListener(snapshot -> {
            int currentSteps = snapshot.child("stepsToday").getValue(Integer.class) != null ? snapshot.child("stepsToday").getValue(Integer.class) : 0;
            int currentTotal = snapshot.child("totalSteps").getValue(Integer.class) != null ? snapshot.child("totalSteps").getValue(Integer.class) : 0;

            Map<String, Object> updates = new HashMap<>();
            updates.put("stepsToday", currentSteps + steps);
            updates.put("totalSteps", currentTotal + steps);
            
            userRef.updateChildren(updates);
            
            // Log history
            DatabaseReference historyRef = userRef.child("history").child(today);
            historyRef.child("steps").setValue(ServerValue.increment(steps));
        });
    }

    public static void addWater(int cups) {
        DatabaseReference userRef = getUserRef();
        if (userRef == null) return;

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        userRef.get().addOnSuccessListener(snapshot -> {
            int currentCups = snapshot.child("waterCups").getValue(Integer.class) != null ? snapshot.child("waterCups").getValue(Integer.class) : 0;
            int currentMana = snapshot.child("mana").getValue(Integer.class) != null ? snapshot.child("mana").getValue(Integer.class) : 0;
            int maxMana = snapshot.child("maxMana").getValue(Integer.class) != null ? snapshot.child("maxMana").getValue(Integer.class) : 50;

            int newCups = currentCups + cups;
            int manaRestore = cups * 5;
            int newMana = Math.min(currentMana + manaRestore, maxMana);

            Map<String, Object> updates = new HashMap<>();
            updates.put("waterCups", newCups);
            updates.put("mana", newMana);

            userRef.updateChildren(updates);
            
            // Log history
            DatabaseReference historyRef = userRef.child("history").child(today);
            historyRef.child("water").setValue(ServerValue.increment(cups));

            addXp(cups * 5);
        });
    }

    public static void updateStreak(int streak) {
        DatabaseReference userRef = getUserRef();
        if (userRef == null) return;
        userRef.child("streak").setValue(streak);
    }

    public static int calculateDamage(int steps) {
        return steps / 10;
    }
}