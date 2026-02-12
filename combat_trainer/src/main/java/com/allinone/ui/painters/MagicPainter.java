package com.allinone.ui.painters;

import com.allinone.framework.Blackboard;
import com.allinone.skills.magic.data.MagicSpell;
import com.allinone.skills.magic.strategies.MagicManager;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;

import java.awt.*;

public class MagicPainter extends SkillPainter {

    public MagicPainter(Blackboard blackboard, long startTime) {
        super(blackboard, startTime, Skill.MAGIC);
    }

    @Override
    protected String getSkillName() {
        return "Magic";
    }

    @Override
    protected Color getBorderColor() {
        return new Color(100, 100, 255); // Blue/Purple
    }

    @Override
    protected void paintCustom(Graphics g, int x, int y) {
        MagicSpell spell = MagicManager.getBestCombatSpell();
        int gained = Skills.getExperience(skill) - startXp;

        double xpPerCast = (spell != null) ? spell.getBaseXp() : 5.5;
        int casts = (int) (gained / xpPerCast);
        int hourly = (int) (casts * 3600000D / (System.currentTimeMillis() - startTime));

        String current = (spell != null) ? spell.getSpellName() : "None";
        g.drawString("Spell: " + current + " | Casts: " + casts + " (" + hourly + "/hr)", x, y);
    }
}
