package com.allinone.ui.painters;

import com.allinone.framework.Blackboard;
import org.dreambot.api.methods.skills.Skill;
import java.awt.*;

import com.allinone.skills.fishing.data.FishingSpot;
import org.dreambot.api.methods.skills.Skills;

public class FishingPainter extends SkillPainter {

    public FishingPainter(Blackboard blackboard, long startTime) {
        super(blackboard, startTime, Skill.FISHING);
    }

    @Override
    protected String getSkillName() {
        return "Fishing";
    }
    
    @Override
    protected Color getBorderColor() {
        return Color.BLUE;
    }
    
    @Override
    protected void paintCustom(Graphics g, int x, int y) {
        int gained = Skills.getExperience(skill) - startXp;
        FishingSpot spot = blackboard.getCurrentFishingSpot();
        double xpPerFish = (spot != null) ? spot.getEstimatedXp() : 10.0;
        
        int fish = (int) (gained / xpPerFish);
        int hourly = (int) (fish * 3600000D / (System.currentTimeMillis() - startTime));
        
        g.drawString("Fish Caught: " + fish + " (" + hourly + "/hr)", x, y);
    }
}
