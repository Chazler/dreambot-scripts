package com.allinone.ui.painters;

import com.allinone.framework.Blackboard;
import com.allinone.skills.cooking.data.CookingItem;
import com.allinone.skills.cooking.strategies.CookingManager;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;

import java.awt.*;

public class CookingPainter extends SkillPainter {

    public CookingPainter(Blackboard blackboard, long startTime) {
        super(blackboard, startTime, Skill.COOKING);
    }

    @Override
    protected String getSkillName() {
        return "Cooking";
    }

    @Override
    protected Color getBorderColor() {
        return new Color(139, 69, 19); // Brown
    }

    @Override
    protected void paintCustom(Graphics g, int x, int y) {
        CookingItem item = CookingManager.getBestItem();
        int gained = Skills.getExperience(skill) - startXp;

        double xpPerCook = (item != null) ? item.getXp() : 30.0;
        int cooked = (int) (gained / xpPerCook);
        int hourly = (int) (cooked * 3600000D / (System.currentTimeMillis() - startTime));

        String target = (item != null) ? item.getCookedName() : "None";
        g.drawString("Cooking: " + target + " | Cooked: " + cooked + " (" + hourly + "/hr)", x, y);
    }
}
