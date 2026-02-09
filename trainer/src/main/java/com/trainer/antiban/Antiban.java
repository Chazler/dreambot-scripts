package com.trainer.antiban;

import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.script.AbstractScript;

public class Antiban {
    private final AbstractScript script;

    public Antiban(AbstractScript script) {
        this.script = script;
    }

    /**
     * Performs a random antiban action based on probability.
     * Tune the probability as needed.
     */
    public void perform() {
        if (Calculations.random(0, 100) < 5) { // 5% chance to perform an action per call
            int action = Calculations.random(0, 5);
            switch (action) {
                case 0:
                    moveMouseOutsideScreen();
                    break;
                case 1:
                    examineRandomObject();
                    break;
                case 2:
                    checkRandomStat();
                    break;
                case 3:
                    moveCameraRandomly();
                    break;
                case 4:
                    // Just a small sleep to simulate hesitation
                    script.sleep(Calculations.random(500, 1500)); 
                    break;
            }
        }
    }

    private void moveMouseOutsideScreen() {
        if (Mouse.isMouseInScreen()) {
            Mouse.moveOutsideScreen();
        }
    }

    private void examineRandomObject() {
        // Placeholder: Actual implementation could find closest object and right-click -> examine
        // For safety/simplicity in V1, we might skip actual interaction to avoid mistakes
        moveCameraRandomly();
    }

    private void checkRandomStat() {
        if (!Tabs.isOpen(Tab.SKILLS)) {
            Tabs.open(Tab.SKILLS);
            script.sleep(600, 1200);
        }
        
        // Hover over a random combat skill
        Skill[] combatSkills = {Skill.ATTACK, Skill.STRENGTH, Skill.DEFENCE, Skill.HITPOINTS, Skill.RANGED, Skill.MAGIC};
        Skill skillToCheck = combatSkills[Calculations.random(0, combatSkills.length)];
        
        Skills.hoverSkill(skillToCheck);
        script.sleep(1000, 3000);
    }

    private void moveCameraRandomly() {
        int currentYaw = Camera.getYaw();
        int currentPitch = Camera.getPitch();
        
        Camera.rotateTo(
            currentYaw + Calculations.random(-50, 50),
            currentPitch + Calculations.random(-20, 20)
        );
    }
}
