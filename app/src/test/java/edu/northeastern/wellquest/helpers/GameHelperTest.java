package edu.northeastern.wellquest.helpers;

import org.junit.Test;
import static org.junit.Assert.*;

public class GameHelperTest {

    @Test
    public void testCalculateDamage() {
        // Base damage logic: 10 steps = 1 damage, minimum 1
        
        // Under 10 steps should deal 1 damage minimum
        assertEquals(1, GameHelper.calculateDamage(0));
        assertEquals(1, GameHelper.calculateDamage(5));
        assertEquals(1, GameHelper.calculateDamage(9));
        
        // Exact multiples of 10
        assertEquals(1, GameHelper.calculateDamage(10));
        assertEquals(5, GameHelper.calculateDamage(50));
        assertEquals(100, GameHelper.calculateDamage(1000));
        
        // Over multiples
        assertEquals(5, GameHelper.calculateDamage(55));
        assertEquals(10, GameHelper.calculateDamage(109));
    }
}