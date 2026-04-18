package edu.northeastern.wellquest.models;

import org.junit.Test;
import static org.junit.Assert.*;

public class MonsterTest {

    @Test
    public void testMonsterInitialization() {
        Monster monster = new Monster("Slime", 100, 20, 0);
        
        assertEquals("Slime", monster.getName());
        assertEquals(100, monster.getHealth());
        assertEquals(100, monster.getMaxHealth());
        assertEquals(20, monster.getXpReward());
        assertEquals(0, monster.getImageResId());
        assertTrue(monster.isAlive());
    }

    @Test
    public void testTakeDamage() {
        Monster monster = new Monster("Slime", 100, 20, 0);
        
        int actualDamage = monster.takeDamage(30);
        assertEquals(30, actualDamage);
        assertEquals(70, monster.getHealth());
        assertTrue(monster.isAlive());
    }

    @Test
    public void testTakeLethalDamage() {
        Monster monster = new Monster("Slime", 100, 20, 0);
        
        int actualDamage = monster.takeDamage(150);
        assertEquals(100, actualDamage); // Damage taken should not exceed health
        assertEquals(0, monster.getHealth());
        assertFalse(monster.isAlive());
    }

    @Test
    public void testMonsterGettersAndSetters() {
        Monster monster = new Monster();
        
        monster.setName("Goblin");
        assertEquals("Goblin", monster.getName());
        
        monster.setHealth(50);
        assertEquals(50, monster.getHealth());
        
        monster.setMaxHealth(150);
        assertEquals(150, monster.getMaxHealth());
        
        monster.setXpReward(50);
        assertEquals(50, monster.getXpReward());
        
        monster.setImageResId(1);
        assertEquals(1, monster.getImageResId());
    }
}