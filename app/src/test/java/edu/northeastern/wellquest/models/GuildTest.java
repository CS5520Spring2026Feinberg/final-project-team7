package edu.northeastern.wellquest.models;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

public class GuildTest {

    @Test
    public void testGuildInitialization() {
        Guild guild = new Guild("g1", "Knights", "u1");
        
        assertEquals("g1", guild.getId());
        assertEquals("Knights", guild.getName());
        assertEquals("u1", guild.getLeaderId());
        assertNotNull(guild.getMembers());
        assertTrue(guild.getMembers().containsKey("u1"));
        assertEquals(10000, guild.getBossHealth());
        assertEquals(10000, guild.getBossMaxHealth());
        assertEquals(0, guild.getCurrentGuildDamage());
    }

    @Test
    public void testGuildGettersAndSetters() {
        Guild guild = new Guild();
        
        guild.setId("g2");
        assertEquals("g2", guild.getId());
        
        guild.setName("Mages");
        assertEquals("Mages", guild.getName());
        
        guild.setLeaderId("u2");
        assertEquals("u2", guild.getLeaderId());
        
        Map<String, Boolean> members = new HashMap<>();
        members.put("u2", true);
        members.put("u3", true);
        guild.setMembers(members);
        assertEquals(2, guild.getMembers().size());
        
        guild.setBossHealth(8000);
        assertEquals(8000, guild.getBossHealth());
        
        guild.setBossMaxHealth(20000);
        assertEquals(20000, guild.getBossMaxHealth());
        
        guild.setCurrentGuildDamage(1500);
        assertEquals(1500, guild.getCurrentGuildDamage());
    }
}