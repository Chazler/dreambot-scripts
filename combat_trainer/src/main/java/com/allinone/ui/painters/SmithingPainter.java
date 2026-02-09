package com.allinone.ui.painters;

import com.allinone.framework.Blackboard;
import com.allinone.skills.smithing.strategies.SmithingManager;
import com.allinone.skills.smithing.data.SmithingItem;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;

import java.awt.*;

public class SmithingPainter extends SkillPainter {

    public SmithingPainter(Blackboard blackboard, long startTime) {
        super(blackboard, startTime, Skill.SMITHING);
    }

    @Override
    protected String getSkillName() {
        return "Smithing";
    }

    @Override
    protected Color getBorderColor() {
        return new Color(192, 192, 192); // Silver/Metal Color
    }

    @Override
    protected void paintCustom(Graphics g, int x, int y) {
        SmithingItem item = SmithingManager.getBestItem();
        int gained = Skills.getExperience(skill) - startXp;
        
        // Rough estimation of items made (XP varies per bar/item)
        // We can just show what we are targeting
        
        String target = (item != null) ? item.getProduceName() : "None";
        
        g.drawString("Target: " + target, x, y);
    }
}
