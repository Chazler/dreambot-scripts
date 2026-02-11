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
    protected void paintCustom(Graphics g, int x, int y) {
        if (!Client.isLoggedIn()) return;

        // Lazy init XP for all combat skills
        if (!initXp) {
            for (Skill s : COMBAT_SKILLS) {
                combatStartXp.put(s, Skills.getExperience(s));
            }
            initXp = true;
        }

        // Multi-skill XP display
        g.setFont(new Font("Consolas", Font.PLAIN, 11));
        int rowY = y;
        for (Skill s : COMBAT_SKILLS) {
            drawCombatSkillRow(g, s, x, rowY);
            rowY += 16;
        }

        // Combat Monitor: Target info + HP bar
        if (Players.getLocal() != null) {
            int col2X = x + 280;

            Character target = Players.getLocal().getInteractingCharacter();
            if (target != null) {
                g.setColor(Color.YELLOW);
                String tName = target.getName();
                if (tName.length() > 15) tName = tName.substring(0, 15);
                g.drawString("Target: " + tName, col2X, y);

                int hpPct = target.getHealthPercent();
                int barY = y + 5;
                g.setColor(new Color(50, 0, 0));
                g.fillRect(col2X, barY, 100, 12);
                g.setColor(hpPct > 50 ? Color.GREEN : (hpPct > 20 ? Color.ORANGE : Color.RED));
                g.fillRect(col2X, barY, hpPct, 12);
                g.setColor(Color.WHITE);
                g.drawRect(col2X, barY, 100, 12);
                g.setFont(new Font("Verdana", Font.BOLD, 9));
                g.drawString(hpPct + "%", col2X + 40, barY + 10);
            } else {
                g.setColor(Color.GRAY);
                g.drawString("Idle / Searching...", col2X, y);
            }
        }
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
