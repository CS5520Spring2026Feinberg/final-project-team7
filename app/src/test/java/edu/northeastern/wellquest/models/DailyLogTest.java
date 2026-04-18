package edu.northeastern.wellquest.models;

import org.junit.Test;
import static org.junit.Assert.*;

public class DailyLogTest {

    @Test
    public void testDailyLogInitialization() {
        DailyLog log = new DailyLog("2026-04-18", 10000, 5);
        
        assertEquals("2026-04-18", log.getDate());
        assertEquals(10000, log.getSteps());
        assertEquals(5, log.getWater());
    }

    @Test
    public void testDailyLogGettersAndSetters() {
        DailyLog log = new DailyLog();
        
        log.setDate("2026-04-19");
        assertEquals("2026-04-19", log.getDate());
        
        log.setSteps(12000);
        assertEquals(12000, log.getSteps());
        
        log.setWater(8);
        assertEquals(8, log.getWater());
    }
}