package edu.northeastern.wellquest.models;

public class Player {
    private String username;
    private String email;
    private int level;
    private int xp;
    private int health;
    private int maxHealth;
    private int mana;
    private int maxMana;
    private int stepsToday;
    private int totalSteps;
    private int streak;
    private int waterCups;
    private int waterGoal;
    private String guildId;

    public Player() {}

    public Player(String username, String email) {
        this.username = username;
        this.email = email;
        this.level = 1;
        this.xp = 0;
        this.health = 100;
        this.maxHealth = 100;
        this.mana = 50;
        this.maxMana = 50;
        this.stepsToday = 0;
        this.totalSteps = 0;
        this.streak = 0;
        this.waterCups = 0;
        this.waterGoal = 8;
        this.guildId = "";
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getXp() { return xp; }
    public void setXp(int xp) { this.xp = xp; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public int getMaxHealth() { return maxHealth; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }

    public int getMana() { return mana; }
    public void setMana(int mana) { this.mana = mana; }

    public int getMaxMana() { return maxMana; }
    public void setMaxMana(int maxMana) { this.maxMana = maxMana; }

    public int getStepsToday() { return stepsToday; }
    public void setStepsToday(int stepsToday) { this.stepsToday = stepsToday; }

    public int getTotalSteps() { return totalSteps; }
    public void setTotalSteps(int totalSteps) { this.totalSteps = totalSteps; }

    public int getStreak() { return streak; }
    public void setStreak(int streak) { this.streak = streak; }

    public int getWaterCups() { return waterCups; }
    public void setWaterCups(int waterCups) { this.waterCups = waterCups; }

    public int getWaterGoal() { return waterGoal; }
    public void setWaterGoal(int waterGoal) { this.waterGoal = waterGoal; }

    public String getGuildId() { return guildId; }
    public void setGuildId(String guildId) { this.guildId = guildId; }

    public int getXpForNextLevel() { return level * 100; }
}