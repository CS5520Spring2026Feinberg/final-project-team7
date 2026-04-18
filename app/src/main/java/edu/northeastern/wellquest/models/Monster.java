package edu.northeastern.wellquest.models;

public class Monster {
    private String name;
    private int health;
    private int maxHealth;
    private int xpReward;
    private int imageResId;

    public Monster() {}

    public Monster(String name, int health, int xpReward, int imageResId) {
        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.xpReward = xpReward;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public int getMaxHealth() { return maxHealth; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }

    public int getXpReward() { return xpReward; }
    public void setXpReward(int xpReward) { this.xpReward = xpReward; }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }

    public boolean isAlive() { return health > 0; }

    public int takeDamage(int damage) {
        int actualDamage = Math.min(damage, health);
        health -= actualDamage;
        return actualDamage;
    }
}