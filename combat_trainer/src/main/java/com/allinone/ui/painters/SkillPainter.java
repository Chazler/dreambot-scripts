package com.allinone.ui.painters;

import com.allinone.framework.Blackboard;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import java.awt.*;

public abstract class SkillPainter {
    protected final Blackboard blackboard;
    protected final long startTime;
    protected final Skill skill;
    protected final int startXp;

    public SkillPainter(Blackboard blackboard, long startTime, Skill skill) {
        this.blackboard = blackboard;
        this.startTime = startTime;
        this.skill = skill;
        this.startXp = Skills.getExperience(skill);
    }

    public void paint(Graphics g) {
        // Chatbox background (approx)
        int x = 7;
        int y = 345;
        int width = 506;
        int height = 129;

        // Background
        g.setColor(new Color(0, 0, 0, 220)); 
        g.fillRect(x, y, width, height);
        
        // Border
        g.setColor(getBorderColor());
        g.drawRect(x, y, width, height);
        
        // Content
        int mx = x + 10;
        int my = y + 20;
        
        g.setColor(Color.ORANGE);
        g.setFont(new Font("Verdana", Font.BOLD, 14));
        g.drawString(getSkillName() + " Trainer", mx, my);
        
        g.setFont(new Font("Verdana", Font.PLAIN, 12));
        g.setColor(Color.WHITE);
        my += 20;
        g.drawString("Time Running: " + formatTime(System.currentTimeMillis() - startTime), mx, my);

        my += 20;
        long remaining = blackboard.getTimeRemaining();
        String remStr = remaining > 0 ? formatTime(remaining) : "Switching...";
        g.drawString("Time Left: " + remStr, mx, my);
        
        my += 20;
        g.drawString("Status: " + blackboard.getCurrentStatus(), mx, my);
        
        // XP Stats
        int currentXp = Skills.getExperience(skill);
        int gained = currentXp - startXp;
        long elapsed = System.currentTimeMillis() - startTime;
        int perHour = (int) ((gained) * 3600000D / elapsed);
        
        my += 20;
        g.drawString("XP Gained: " + gained + " (" + perHour + "/hr)", mx, my);
        g.drawString("Level: " + Skills.getRealLevel(skill) + " (+"+ (Skills.getRealLevel(skill) - getStartLevel()) +")", mx + 200, my);
        
        // Custom Hook
        my += 20;
        paintCustom(g, mx, my);
    }
    
    protected void paintCustom(Graphics g, int x, int y) {
        // Default no-op
    }
    
    protected Color getBorderColor() {
        return Color.GREEN;
    }
    
    protected abstract String getSkillName();
    
    private int getStartLevel() {
        // Approximate start level (not perfectly accurate if we didn't cache it, but standard approach)
        // Ideally we cache start level in constructor
        return Skills.getLevelForExperience(startXp);
    }

    protected String formatTime(long ms) {
        long s = ms / 1000, m = s / 60, h = m / 60;
        s %= 60; m %= 60; h %= 24;
        return String.format("%02d:%02d:%02d", h, m, s);
    }
}
