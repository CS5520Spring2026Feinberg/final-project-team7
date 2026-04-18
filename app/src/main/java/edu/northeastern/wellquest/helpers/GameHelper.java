package edu.northeastern.wellquest.helpers;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GameHelper {

    private static final String TAG = "GameHelper";
    private static DatabaseReference getDB() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public static String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
    }

    public static DatabaseReference getUserRef() {
        String uid = getCurrentUserId();
        if (uid == null) return null;
        return getDB().child("users").child(uid);
    }

    public static void addXp(int amount) {
        DatabaseReference userRef = getUserRef();
        if (userRef == null) return;

        userRef.get().addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()) return;

            int currentXp = snapshot.child("xp").getValue(Integer.class) != null ? snapshot.child("xp").getValue(Integer.class) : 0;
            int currentLevel = snapshot.child("level").getValue(Integer.class) != null ? snapshot.child("level").getValue(Integer.class) : 1;
            int health = snapshot.child("health").getValue(Integer.class) != null ? snapshot.child("health").getValue(Integer.class) : 100;
            int mana = snapshot.child("mana").getValue(Integer.class) != null ? snapshot.child("mana").getValue(Integer.class) : 50;

            int totalXp = currentXp + amount;
            int newLevel = currentLevel;

            // Calculate multi-level ups
            while (totalXp >= (newLevel * 100)) {
                totalXp -= (newLevel * 100);
                newLevel++;
            }

            Map<String, Object> updates = new HashMap<>();
            if (newLevel > currentLevel) {
                int newMaxHealth = 100 + (newLevel - 1) * 10;
                int newMaxMana = 50 + (newLevel - 1) * 5;

                updates.put("level", newLevel);
                updates.put("xp", totalXp);
                updates.put("maxHealth", newMaxHealth);
                updates.put("health", newMaxHealth); // Full heal on level up
                updates.put("maxMana", newMaxMana);
                updates.put("mana", newMaxMana);     // Full mana on level up
            } else {
                updates.put("xp", totalXp);
            }

            userRef.updateChildren(updates).addOnFailureListener(e -> Log.e(TAG, "Failed to update XP", e));
        }).addOnFailureListener(e -> Log.e(TAG, "Failed to fetch user for XP update", e));
    }

    public static void addSteps(int steps) {
        DatabaseReference userRef = getUserRef();
        if (userRef == null || steps <= 0) return;

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        userRef.get().addOnSuccessListener(snapshot -> {
            int currentSteps = snapshot.child("stepsToday").getValue(Integer.class) != null ? snapshot.child("stepsToday").getValue(Integer.class) : 0;
            int currentTotal = snapshot.child("totalSteps").getValue(Integer.class) != null ? snapshot.child("totalSteps").getValue(Integer.class) : 0;

            Map<String, Object> updates = new HashMap<>();
            updates.put("stepsToday", currentSteps + steps);
            updates.put("totalSteps", currentTotal + steps);
            
            userRef.updateChildren(updates);
            
            // Log history using ServerValue.increment for atomicity
            DatabaseReference historyRef = userRef.child("history").child(today);
            historyRef.child("steps").setValue(ServerValue.increment(steps));

            // Update Guild Raid Boss Damage
            String guildId = snapshot.child("guildId").getValue(String.class);
            if (guildId != null && !guildId.isEmpty()) {
                int damage = calculateDamage(steps);
                if (damage > 0) {
                    DatabaseReference guildRef = getDB().child("guilds").child(guildId);
                    guildRef.get().addOnSuccessListener(guildSnapshot -> {
                        int currentDmg = guildSnapshot.child("currentGuildDamage").getValue(Integer.class) != null 
                            ? guildSnapshot.child("currentGuildDamage").getValue(Integer.class) : 0;
                        int bossHP = guildSnapshot.child("bossHealth").getValue(Integer.class) != null 
                            ? guildSnapshot.child("bossHealth").getValue(Integer.class) : 10000;
                        
                        int newTotalDmg = currentDmg + damage;
                        
                        if (newTotalDmg >= bossHP) {
                            // Boss defeated!
                            handleBossDefeat(guildId, bossHP);
                        } else {
                            guildRef.child("currentGuildDamage").setValue(ServerValue.increment(damage));
                        }
                    });
                }
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Failed to update steps", e));
    }

    private static void handleBossDefeat(String guildId, int oldMax) {
        DatabaseReference guildRef = getDB().child("guilds").child(guildId);
        
        Map<String, Object> reset = new HashMap<>();
        reset.put("currentGuildDamage", 0);
        int newMax = (int)(oldMax * 1.5); // Next boss is 50% tougher
        reset.put("bossHealth", newMax);
        reset.put("bossMaxHealth", newMax);
        
        guildRef.updateChildren(reset);
        
        // Reward all guild members with massive XP
        guildRef.child("members").get().addOnSuccessListener(snapshot -> {
            for (DataSnapshot member : snapshot.getChildren()) {
                String memberId = member.getKey();
                if (memberId != null) {
                    getDB().child("users").child(memberId).child("xp").setValue(ServerValue.increment(1000));
                }
            }
        });
    }

    public static void addWater(int cups) {
        DatabaseReference userRef = getUserRef();
        if (userRef == null || cups <= 0) return;

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
            
            DatabaseReference historyRef = userRef.child("history").child(today);
            historyRef.child("water").setValue(ServerValue.increment(cups));

            addXp(cups * 5); // Water gives small amount of XP
        }).addOnFailureListener(e -> Log.e(TAG, "Failed to update water", e));
    }

    public static void updateStreak(int streak) {
        DatabaseReference userRef = getUserRef();
        if (userRef == null) return;
        userRef.child("streak").setValue(streak).addOnFailureListener(e -> Log.e(TAG, "Failed to update streak", e));
    }

    public static int calculateDamage(int steps) {
        // Base damage logic: 10 steps = 1 damage, minimum 1
        return Math.max(1, steps / 10);
    }

    // --- Guild Methods ---

    public static void createGuild(String guildName) {
        String uid = getCurrentUserId();
        if (uid == null || guildName == null || guildName.trim().isEmpty()) return;

        DatabaseReference newGuildRef = getDB().child("guilds").push();
        String guildId = newGuildRef.getKey();
        if (guildId == null) return;

        edu.northeastern.wellquest.models.Guild newGuild = new edu.northeastern.wellquest.models.Guild(guildId, guildName.trim(), uid);
        newGuildRef.setValue(newGuild).addOnSuccessListener(aVoid -> {
            joinGuild(guildId);
        });
    }

    public static void joinGuild(String guildId) {
        String uid = getCurrentUserId();
        if (uid == null || guildId == null || guildId.isEmpty()) return;

        getDB().child("guilds").child(guildId).child("members").child(uid).setValue(true)
            .addOnSuccessListener(aVoid -> {
                DatabaseReference userRef = getUserRef();
                if (userRef != null) {
                    userRef.child("guildId").setValue(guildId);
                }
            });
    }

    public static void leaveGuild(String guildId) {
        String uid = getCurrentUserId();
        if (uid == null || guildId == null || guildId.isEmpty()) return;

        getDB().child("guilds").child(guildId).child("members").child(uid).removeValue()
            .addOnSuccessListener(aVoid -> {
                DatabaseReference userRef = getUserRef();
                if (userRef != null) {
                    userRef.child("guildId").setValue("");
                }
            });
    }
}