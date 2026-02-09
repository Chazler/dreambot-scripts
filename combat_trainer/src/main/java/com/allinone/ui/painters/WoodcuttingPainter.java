package com.allinone.ui.painters;

import com.allinone.framework.Blackboard;
import org.dreambot.api.methods.skills.Skill;
import java.awt.*;

import com.allinone.skills.woodcutting.data.WoodcuttingSpot;
import org.dreambot.api.methods.skills.Skills;

public class WoodcuttingPainter extends SkillPainter {

    public WoodcuttingPainter(Blackboard blackboard, long startTime) {
        super(blackboard, startTime, Skill.WOODCUTTING);
    }

    @Override
    protected String getSkillName() {
        return "Woodcutting";
    }
    
    @Override
    protected Color getBorderColor() {
        return new Color(139, 69, 19); // Brown
    }
    
    @Override
    protected void paintCustom(Graphics g, int x, int y) {
        int gained = Skills.getExperience(skill) - startXp;
        WoodcuttingSpot spot = blackboard.getCurrentWoodcuttingSpot();
        double xpPerLog = (spot != null) ? spot.getTreeType().getXp() : 25.0; // Default to normal tree
        
        int logs = (int) (gained / xpPerLog);
        int hourly = (int) (logs * 3600000D / (System.currentTimeMillis() - startTime));
        
        g.drawString("Logs Gathered: " + logs + " (" + hourly + "/hr)", x, y);
    }
}
