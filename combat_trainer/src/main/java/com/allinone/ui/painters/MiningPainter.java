package com.allinone.ui.painters;

import com.allinone.framework.Blackboard;
import com.allinone.skills.mining.data.MiningSpot;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;

import java.awt.*;

public class MiningPainter extends SkillPainter {

    public MiningPainter(Blackboard blackboard, long startTime) {
        super(blackboard, startTime, Skill.MINING);
    }

    @Override
    protected String getSkillName() {
        return "Mining";
    }

    @Override
    protected Color getBorderColor() {
        return new Color(139, 69, 19); // Brown-ish for mining rocks
    }

    @Override
    protected void paintCustom(Graphics g, int x, int y) {
        int gained = Skills.getExperience(skill) - startXp;
        MiningSpot spot = blackboard.getCurrentMiningSpot();
        
        double xpPerOre = (spot != null && spot.getRockType() != null) ? spot.getRockType().getXp() : 17.5; // Default to Copper/Tin
        
        int ores = (int) (gained / xpPerOre);
        long elapsedTime = System.currentTimeMillis() - startTime;
        int hourly = (elapsedTime > 0) ? (int) (ores * 3600000D / elapsedTime) : 0;
        
        g.drawString("Mined: " + ores + " (" + hourly + "/hr) | " + ((spot != null) ? spot.getName() : "Searching"), x, y);
        // g.drawString("Ores Mined: " + ores + " (" + hourly + "/hr)", x, y + 20);
    }
}
