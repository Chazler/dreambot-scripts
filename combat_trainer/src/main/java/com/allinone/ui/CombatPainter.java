package com.allinone.ui;

import com.allinone.framework.Blackboard;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.Client;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CombatPainter {

    private final Blackboard blackboard;
    private final long startTime;
    private final Map<Skill, Integer> startXp = new HashMap<>();
    private boolean initXp = false;

    public CombatPainter(Blackboard blackboard, long startTime) {
        this.blackboard = blackboard;
        this.startTime = startTime;
    }

    public void paint(Graphics g) {
        if (!Client.isLoggedIn()) return;
        
        // Lazy Init XP
        if (!initXp) {
            for (Skill s : new Skill[]{Skill.ATTACK, Skill.STRENGTH, Skill.DEFENCE, Skill.HITPOINTS}) {
                startXp.put(s, Skills.getExperience(s));
            }
            initXp = true;
        }

        // Chatbox area (Fixed mode approx)
        int x = 7;
        int y = 345;
        int width = 506;
        int height = 129;

        // Background
        g.setColor(new Color(0, 0, 0, 240)); 
        g.fillRect(x, y, width, height);
        
        // Stylish Border
        g.setColor(new Color(0, 100, 200));
        g.drawRect(x, y, width, height);
        g.setColor(new Color(0, 50, 100));
        g.drawRect(x + 2, y + 2, width - 4, height - 4);

        // --- Column 1: General Info ---
        int col1X = x + 15;
        int rowH = 18;
        int curY = y + 25;

        g.setColor(new Color(255, 160, 0)); // Title color (Orange)
        g.setFont(new Font("Verdana", Font.BOLD, 13));
        g.drawString("Combat Trainer v1.1", col1X, curY);
        
        g.setFont(new Font("Verdana", Font.PLAIN, 11));
        g.setColor(new Color(200, 200, 200));
        curY += rowH + 5;
        
        g.drawString("Time: " + formatTime(System.currentTimeMillis() - startTime), col1X, curY);
        curY += rowH;
        
        long remaining = blackboard != null ? blackboard.getTimeRemaining() : 0;
        String remStr = remaining > 0 ? formatTime(remaining) : "--:--:--";
        // Only show if positive to avoid clutter if undefined
        if (remaining > 0) {
            g.drawString("Left: " + remStr, col1X, curY);
            curY += rowH;
        }

        String status = blackboard != null ? blackboard.getCurrentStatus() : "Initializing...";
        if (status.length() > 25) status = status.substring(0, 22) + "..."; // Truncate long status
        g.drawString("Status: " + status, col1X, curY);
        curY += rowH;
        
        String locName = (blackboard != null && blackboard.getBestLocation() != null) ? blackboard.getBestLocation().getName() : "None";
        g.drawString("Loc: " + locName, col1X, curY);

        // --- Column 2: XP Tracker ---
        int col2X = x + 180;
        curY = y + 25;
        g.setFont(new Font("Verdana", Font.BOLD, 12));
        g.setColor(new Color(100, 200, 255));
        g.drawString("XP Gains", col2X, curY);
        
        g.setFont(new Font("Consolas", Font.PLAIN, 11)); // Monospace for numbers
        curY += 5;
        
        drawSkillRow(g, Skill.ATTACK, col2X, curY += rowH);
        drawSkillRow(g, Skill.STRENGTH, col2X, curY += rowH);
        drawSkillRow(g, Skill.DEFENCE, col2X, curY += rowH);
        drawSkillRow(g, Skill.HITPOINTS, col2X, curY += rowH);


        // --- Column 3: Combat Monitor ---
        int col3X = x + 350;
        curY = y + 25;
        g.setFont(new Font("Verdana", Font.BOLD, 12));
        g.setColor(new Color(255, 100, 100)); // Red tint
        g.drawString("Combat Monitor", col3X, curY);
        
        g.setFont(new Font("Verdana", Font.PLAIN, 11));
        curY += rowH;
        
        if (Players.getLocal() != null) {
            // Player HP
            int hp = Skills.getBoostedLevel(Skill.HITPOINTS);
            int maxHp = Skills.getRealLevel(Skill.HITPOINTS);
            g.setColor(Color.WHITE);
            g.drawString("My HP: " + hp + "/" + maxHp, col3X, curY);
            curY += rowH;

            // Target Info
            Character target = Players.getLocal().getInteractingCharacter();
            if (target != null) {
                g.setColor(Color.YELLOW);
                String tName = target.getName();
                if (tName.length() > 15) tName = tName.substring(0, 15);
                g.drawString("Target: " + tName, col3X, curY);
                curY += 5;
                
                // Health Bar
                int hpPct = target.getHealthPercent();
                g.setColor(new Color(50, 0, 0));
                g.fillRect(col3X, curY, 100, 12); // Bg
                
                // Color gradient based on HP
                g.setColor(hpPct > 50 ? Color.GREEN : (hpPct > 20 ? Color.ORANGE : Color.RED));
                g.fillRect(col3X, curY, hpPct, 12); 
                
                g.setColor(Color.WHITE);
                g.drawRect(col3X, curY, 100, 12);
                g.setFont(new Font("Verdana", Font.BOLD, 9));
                String hpText = hpPct + "%";
                // Center text roughly
                g.drawString(hpText, col3X + 50 - (hpText.length() * 3), curY + 10);
            } else {
                g.setColor(Color.GRAY);
                g.drawString("Idle / Searching...", col3X, curY);
            }
        }
    }

    private void drawSkillRow(Graphics g, Skill skill, int x, int y) {
        int currentXp = Skills.getExperience(skill);
        int startXpVal = startXp.getOrDefault(skill, currentXp);
        int xpGain = currentXp - startXpVal;
        
        int currentLvl = Skills.getRealLevel(skill);
        int startLvl = Skills.getLevelForExperience(startXpVal);
        int lvlGain = currentLvl - startLvl;
        
        String name = skill.getName().substring(0, 3);
        
        if (xpGain > 0) {
            long runTime = System.currentTimeMillis() - startTime;
            int hourly = (int) (xpGain * 3600000.0 / Math.max(runTime, 1));
            
            // Format: "Str: 55(+1) 45,000/h"
            String text = String.format("%s: %d(+%d) %,d/h", name, currentLvl, lvlGain, hourly);
            
            g.setColor(Color.GREEN);
            g.drawString(text, x, y);
        } else {
             g.setColor(Color.DARK_GRAY);
             g.drawString(String.format("%s: %d(+0)", name, currentLvl), x, y);
        }
    }

    private String formatTime(long ms) {
        long s = (ms / 1000) % 60;
        long m = (ms / (1000 * 60)) % 60;
        long h = (ms / (1000 * 60 * 60));
        return String.format("%02d:%02d:%02d", h, m, s);
    }
}
