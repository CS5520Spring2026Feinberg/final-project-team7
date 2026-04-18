package edu.northeastern.wellquest.models;

import org.junit.Test;
import static org.junit.Assert.*;

public class PlayerTest {

    @Test
    public void testPlayerInitialization() {
        Player player = new Player("Hero", "hero@test.com");
        
        assertEquals("Hero", player.getUsername());
        assertEquals("hero@test.com", player.getEmail());
        assertEquals(1, player.getLevel());
        assertEquals(0, player.getXp());
        assertEquals(100, player.getHealth());
        assertEquals(100, player.getMaxHealth());
        assertEquals(50, player.getMana());
        assertEquals(50, player.getMaxMana());
        assertEquals(0, player.getStepsToday());
        assertEquals(0, player.getTotalSteps());
        assertEquals(0, player.getStreak());
        assertEquals(0, player.getWaterCups());
        assertEquals(8, player.getWaterGoal());
        assertEquals("", player.getGuildId());
    }

    @Test
    public void testPlayerGettersAndSetters() {
        Player player = new Player();
        
        player.setUsername("TestUser");
        assertEquals("TestUser", player.getUsername());
        
        player.setHealth(80);
        assertEquals(80, player.getHealth());
        
        player.setMana(30);
        assertEquals(30, player.getMana());
        
        player.setLevel(5);
        assertEquals(5, player.getLevel());
        
        player.setStepsToday(5000);
        assertEquals(5000, player.getStepsToday());
        
        player.setGuildId("guild-123");
        assertEquals("guild-123", player.getGuildId());
    }

    @Test
    public void testGetXpForNextLevel() {
        Player player = new Player("Hero", "test@test.com");
        assertEquals(100, player.getXpForNextLevel());
        
        player.setLevel(5);
        assertEquals(500, player.getXpForNextLevel());
    }
}