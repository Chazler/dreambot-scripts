package com.allinone.ui.painters;

import com.allinone.framework.Blackboard;
import org.dreambot.api.methods.skills.Skill;
import java.awt.*;

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
}
