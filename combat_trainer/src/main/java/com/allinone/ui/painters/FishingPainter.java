package com.allinone.ui.painters;

import com.allinone.framework.Blackboard;
import org.dreambot.api.methods.skills.Skill;
import java.awt.*;

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
}
