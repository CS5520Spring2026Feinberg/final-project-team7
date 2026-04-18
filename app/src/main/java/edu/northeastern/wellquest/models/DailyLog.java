package edu.northeastern.wellquest.models;

public class DailyLog {
    private String date;
    private int steps;
    private int water;

    public DailyLog() {}

    public DailyLog(String date, int steps, int water) {
        this.date = date;
        this.steps = steps;
        this.water = water;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getSteps() { return steps; }
    public void setSteps(int steps) { this.steps = steps; }

    public int getWater() { return water; }
    public void setWater(int water) { this.water = water; }
}