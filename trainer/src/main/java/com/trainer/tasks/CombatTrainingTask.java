package com.trainer.tasks;

import com.trainer.framework.Goal;
import com.trainer.framework.GoalManager;
import com.trainer.framework.Task;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.input.Camera;

public class CombatTrainingTask implements Task {
    private final AbstractScript script;
    private final GoalManager goalManager;
    private final String[] targetNames = {"Goblin"};
    private final Area GOBLIN_AREA = new Area(3239, 3254, 3267, 3226); // Lumbridge East Goblins

    public CombatTrainingTask(AbstractScript script, GoalManager goalManager) {
        this.script = script;
        this.goalManager = goalManager;
    }

    @Override
    public boolean accept() {
        Goal goal = goalManager.getCurrentGoal();
        if (goal == null) return false;
        
        switch (goal.getSkill()) {
            case ATTACK:
            case STRENGTH:
            case DEFENCE:
            case RANGED:
            case MAGIC:
                return !Players.getLocal().isInCombat();
            default:
                return false;
        }
    }

    @Override
    public int execute() {
        // Safety: If low health and no food, defer combat goals!
        if (Players.getLocal().getHealthPercent() < 40 && !Inventory.contains(i -> i != null && i.hasAction("Eat"))) {
             script.log("Health critical (" + Players.getLocal().getHealthPercent() + "%) and no food. Deferring combat task.");
             goalManager.deferCurrentGoal();
             return 1000;
        }

        Goal goal = goalManager.getCurrentGoal();
        CombatStyle targetStyle = getTargetStyle(goal.getSkill());

        // Ensure style
        if (Combat.getCombatStyle() != targetStyle) {
            script.log("Switching style for " + goal.getSkill());
            if (!Tabs.isOpen(Tab.COMBAT)) {
                Tabs.open(Tab.COMBAT);
                return Calculations.random(300, 600);
            }
            if (Combat.setCombatStyle(targetStyle)) {
                return Calculations.random(1000, 2000);
            }
        }

        // Fight
        NPC target = NPCs.closest(n -> 
            n != null && 
            !n.isInCombat() && 
            isTargetName(n.getName()) &&
            n.canReach()
        );

        if (target != null) {
            if (!target.isOnScreen()) {
                Camera.rotateToEntity(target);
            }
            if (target.interact("Attack")) {
                script.sleepUntil(() -> Players.getLocal().isInCombat(), 5000, 100);
            }
        } else {
             if (!GOBLIN_AREA.contains(Players.getLocal())) {
                 script.log("Walking to Goblin Area...");
                 Walking.walk(GOBLIN_AREA.getRandomTile());
                 return Calculations.random(1000, 2000);
             }
             script.log("No suitable targets found nearby.");
        }
        
        return Calculations.random(600, 1000);
    }
    
    private CombatStyle getTargetStyle(Skill skill) {
        switch (skill) {
            case ATTACK: return CombatStyle.ATTACK;
            case STRENGTH: return CombatStyle.STRENGTH;
            case DEFENCE: return CombatStyle.DEFENCE;
            case RANGED: return CombatStyle.RANGED;
            case MAGIC: return CombatStyle.MAGIC;
            default: return CombatStyle.ATTACK;
        }
    }

    private boolean isTargetName(String name) {
        if (name == null) return false;
        for (String t : targetNames) {
            if (t.equals(name)) return true;
        }
        return false;
    }

    @Override
    public String status() {
        return "Combat Training";
    }
}
