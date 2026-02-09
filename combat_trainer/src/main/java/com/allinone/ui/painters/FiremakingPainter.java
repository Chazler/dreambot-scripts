package com.allinone.ui.painters;

import com.allinone.framework.Blackboard;
import org.dreambot.api.methods.skills.Skill;
import java.awt.*;

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
}
