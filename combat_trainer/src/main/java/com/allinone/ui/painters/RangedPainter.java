package com.allinone.ui.painters;

import com.allinone.framework.Blackboard;
import com.allinone.skills.ranged.data.RangedTrainingLocation;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.Client;

import java.awt.*;

public class RangedPainter extends SkillPainter {

    public RangedPainter(Blackboard blackboard, long startTime) {
        super(blackboard, startTime, Skill.RANGED);
    }

    @Override
    protected String getSkillName() {
        return "Ranged";
    }

    @Override
    protected Color getBorderColor() {
        return new Color(0, 128, 0); // Green
    }

    @Override
    protected void paintCustom(Graphics g, int x, int y) {
        if (!Client.isLoggedIn()) return;

        RangedTrainingLocation loc = blackboard.getCurrentRangedLocation();
        String locName = (loc != null) ? loc.getName() : "None";

        g.drawString("Location: " + locName, x, y);

        // Show target info
        if (Players.getLocal() != null) {
            Character target = Players.getLocal().getInteractingCharacter();
            if (target != null) {
                int col2X = x + 280;
                g.setColor(Color.YELLOW);
                String tName = target.getName();
                if (tName.length() > 15) tName = tName.substring(0, 15);
                g.drawString("Target: " + tName + " (" + target.getHealthPercent() + "%)", col2X, y);
            }
        }
    }
}
