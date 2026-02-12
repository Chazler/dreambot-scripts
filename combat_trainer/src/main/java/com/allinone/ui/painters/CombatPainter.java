package com.allinone.ui.painters;

import com.allinone.framework.Blackboard;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.Client;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CombatPainter extends SkillPainter {

    private final Map<Skill, Integer> combatStartXp = new HashMap<>();
    private boolean initXp = false;

    private static final Skill[] COMBAT_SKILLS = {Skill.ATTACK, Skill.STRENGTH, Skill.DEFENCE, Skill.HITPOINTS};

    public CombatPainter(Blackboard blackboard, long startTime) {
        super(blackboard, startTime, Skill.ATTACK);
    }

    @Override
    protected String getSkillName() {
        return "Combat";
    }

    @Override
    protected Color getBorderColor() {
        return new Color(0, 100, 200);
    }

    @Override
    public void paint(Graphics g) {
        if (!Client.isLoggedIn()) return;

        // Lazy init XP for all combat skills
        if (!initXp) {
            for (Skill s : COMBAT_SKILLS) {
                combatStartXp.put(s, Skills.getExperience(s));
            }
            initXp = true;
        }

        // Chatbox background
        int x = 7;
        int y = 345;
        int width = 506;
        int height = 129;

        g.setColor(new Color(0, 0, 0, 220));
        g.fillRect(x, y, width, height);

        g.setColor(getBorderColor());
        g.drawRect(x, y, width, height);

        // -- LEFT COLUMN (Status & Info) --
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

        // Combat Monitor (Target Info)
        my += 20;
        if (Players.getLocal() != null) {
            Character target = Players.getLocal().getInteractingCharacter();
            if (target != null) {
                String tName = target.getName();
                if (tName != null && tName.length() > 15) tName = tName.substring(0, 15);
                g.setColor(Color.YELLOW);
                g.drawString("Target: " + (tName != null ? tName : "Unknown"), mx, my);

                int hpPct = target.getHealthPercent();
                // Draw simple bar below
                int barY = my + 5;
                int barW = 100;
                int barH = 10;
                
                g.setColor(new Color(50, 0, 0));
                g.fillRect(mx, barY, barW, barH);
                
                g.setColor(hpPct > 50 ? Color.GREEN : (hpPct > 20 ? Color.ORANGE : Color.RED));
                g.fillRect(mx, barY, (int)(barW * (hpPct / 100.0)), barH);
                
                g.setColor(Color.WHITE);
                g.drawRect(mx, barY, barW, barH);
            } else {
                g.setColor(Color.GRAY);
                g.drawString("Target: None", mx, my);
            }
        }

        // -- RIGHT COLUMN (XP & Stats) --
        int cx = x + 230; // Shift right
        int cy = y + 20;

        // Calculate Total XP
        int totalXpGained = 0;
        for (Skill s : COMBAT_SKILLS) {
            int start = combatStartXp.getOrDefault(s, 0);
            int current = Skills.getExperience(s);
            if (current > start) {
                totalXpGained += (current - start);
            }
        }
        
        long runTime = System.currentTimeMillis() - startTime;
        int totalHourly = (int) (totalXpGained * 3600000.0 / Math.max(runTime, 1));

        g.setColor(Color.WHITE);
        g.setFont(new Font("Verdana", Font.BOLD, 12));
        g.drawString(String.format("Total XP: %,d (%,d/h)", totalXpGained, totalHourly), cx, cy);

        // Individual Skills
        cy += 20;
        g.setFont(new Font("Consolas", Font.PLAIN, 11));
        
        for (Skill s : COMBAT_SKILLS) {
            drawCombatSkillRow(g, s, cx, cy);
            cy += 15;
        }
    }

    @Override
    protected void paintCustom(Graphics g, int x, int y) {
        // No-op, we overrode paint()
    }

    private void drawCombatSkillRow(Graphics g, Skill skill, int x, int y) {
        int currentXp = Skills.getExperience(skill);
        int startXpVal = combatStartXp.getOrDefault(skill, currentXp);
        int xpGain = currentXp - startXpVal;

        int currentLvl = Skills.getRealLevel(skill);
        int startLvl = Skills.getLevelForExperience(startXpVal);
        int lvlGain = currentLvl - startLvl;

        String name = skill.getName().substring(0, 3);

        if (xpGain > 0) {
            long runTime = System.currentTimeMillis() - startTime;
            int hourly = (int) (xpGain * 3600000.0 / Math.max(runTime, 1));

            g.setColor(Color.GREEN);
            g.drawString(String.format("%s: %d(+%d) %,d/h", name, currentLvl, lvlGain, hourly), x, y);
        } else {
            g.setColor(Color.DARK_GRAY);
            g.drawString(String.format("%s: %d(+0)", name, currentLvl), x, y);
        }
    }
}
