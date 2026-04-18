package edu.northeastern.wellquest.models;

import java.util.HashMap;
import java.util.Map;

public class Guild {
    private String id;
    private String name;
    private String leaderId;
    private Map<String, Boolean> members;
    private int bossHealth;
    private int bossMaxHealth;
    private int currentGuildDamage;

    public Guild() {
        members = new HashMap<>();
    }

    public Guild(String id, String name, String leaderId) {
        this.id = id;
        this.name = name;
        this.leaderId = leaderId;
        this.members = new HashMap<>();
        this.members.put(leaderId, true);
        this.bossMaxHealth = 10000;
        this.bossHealth = 10000;
        this.currentGuildDamage = 0;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLeaderId() { return leaderId; }
    public void setLeaderId(String leaderId) { this.leaderId = leaderId; }
    public Map<String, Boolean> getMembers() { return members; }
    public void setMembers(Map<String, Boolean> members) { this.members = members; }
    public int getBossHealth() { return bossHealth; }
    public void setBossHealth(int bossHealth) { this.bossHealth = bossHealth; }
    public int getBossMaxHealth() { return bossMaxHealth; }
    public void setBossMaxHealth(int bossMaxHealth) { this.bossMaxHealth = bossMaxHealth; }
    public int getCurrentGuildDamage() { return currentGuildDamage; }
    public void setCurrentGuildDamage(int currentGuildDamage) { this.currentGuildDamage = currentGuildDamage; }
}