package com.allinone.ui.painters;

import com.allinone.framework.Blackboard;
import com.allinone.skills.crafting.data.CraftingItem;
import com.allinone.skills.crafting.strategies.CraftingManager;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;

import java.awt.*;

public class CraftingPainter extends SkillPainter {

    public CraftingPainter(Blackboard blackboard, long startTime) {
        super(blackboard, startTime, Skill.CRAFTING);
    }

    @Override
    protected String getSkillName() {
        return "Crafting";
    }

    @Override
    protected Color getBorderColor() {
        return new Color(150, 100, 50); // Leather brown
    }

    @Override
    protected void paintCustom(Graphics g, int x, int y) {
        CraftingItem item = CraftingManager.getBestItem();
        int gained = Skills.getExperience(skill) - startXp;

        double xpPer = (item != null) ? item.getXp() : 13.8;
        int crafted = (int) (gained / xpPer);
        int hourly = (int) (crafted * 3600000D / (System.currentTimeMillis() - startTime));

        String target = (item != null) ? item.getProduceName() : "None";
        g.drawString("Crafting: " + target + " | Made: " + crafted + " (" + hourly + "/hr)", x, y);
    }
}
