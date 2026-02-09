package com.allinone.ui.painters;

import com.allinone.framework.Blackboard;
import org.dreambot.api.methods.skills.Skill;
import java.awt.*;

import com.allinone.skills.firemaking.data.FiremakingLog;
import org.dreambot.api.methods.skills.Skills;

public class FiremakingPainter extends SkillPainter {

    public FiremakingPainter(Blackboard blackboard, long startTime) {
        super(blackboard, startTime, Skill.FIREMAKING);
    }

    @Override
    protected String getSkillName() {
        return "Firemaking";
    }

    @Override
    protected Color getBorderColor() {
        return Color.RED;
    }
    
    @Override
    protected void paintCustom(Graphics g, int x, int y) {
        int gained = Skills.getExperience(skill) - startXp;
        FiremakingLog currentLog = blackboard.getCurrentFiremakingLog();
        double xpPerLog = (currentLog != null) ? currentLog.getXp() : 40.0; // Default to normal logs
        
        int logs = (int) (gained / xpPerLog);
        int hourly = (int) (logs * 3600000D / (System.currentTimeMillis() - startTime));
        
        g.drawString("Logs Burned: " + logs + " (" + hourly + "/hr)", x, y);
    }
}
