package com.trainer.tasks;

import com.trainer.framework.Goal;
import com.trainer.framework.GoalManager;
import com.trainer.framework.Task;
import com.trainer.framework.StuckManager;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.input.Camera;

import com.trainer.framework.Movement;
import com.trainer.data.TrainingArea;
import com.trainer.framework.InventoryManager;

import com.trainer.framework.StuckManager;

public class WoodcuttingTask implements Task {
    private final AbstractScript script;
    private final GoalManager goalManager;
    private final StuckManager stuckManager = new StuckManager(); 

    public WoodcuttingTask(AbstractScript script, GoalManager goalManager) {
        this.script = script;
        this.goalManager = goalManager;
    }

    @Override
    public boolean accept() {
        Goal goal = goalManager.getCurrentGoal();
        return goal != null && goal.getSkill() == Skill.WOODCUTTING;
    }

    @Override
    public int execute() {
        if (Inventory.isFull()) {
            Goal goal = goalManager.getCurrentGoal();
            if (goal != null && goal.getMode() == Goal.TrainingMode.DROP) {
                script.log("Inventory full, dropping logs.");
                Inventory.dropAll(i -> i.getName().endsWith("logs"));
                return Calculations.random(1000, 2000);
            }
            return 1000; // Let BankTask handle it
        }

        if (!InventoryManager.prepare(script, goalManager)) {
            return 1000;
        }

        // Centralized Area Check
        Goal goal = goalManager.getCurrentGoal();
        Area targetArea = TrainingArea.getArea(goal);
        if (targetArea != null && !targetArea.contains(Players.getLocal())) {
            script.log("Walking to Woodcutting Area...");
            Movement.walkTo(targetArea);
            return 1000;
        }

        if (Players.getLocal().isAnimating()) {
            stuckManager.reset();
            return Calculations.random(1000, 2000);
        }

        String treeName = getTreeName();
        GameObject tree = GameObjects.closest(t -> t.getName().equals(treeName) && !stuckManager.isBlacklisted(t));

        if (tree != null && tree.distance(Players.getLocal()) < 10) {
            if (!tree.isOnScreen()) {
                Camera.rotateToEntity(tree);
            }
            if (tree.interact("Chop down")) {
                if (script.sleepUntil(() -> Players.getLocal().isAnimating(), 5000, 100)) {
                    stuckManager.reset();
                    script.sleepUntil(() -> !Players.getLocal().isAnimating(), 30000, 1000);
                } else {
                    stuckManager.trackFailure(tree);
                }
            }
        }
        
        return Calculations.random(800, 1200);
    }
    
    private String getTreeName() {
        int level = Skills.getRealLevel(Skill.WOODCUTTING);
        if (level >= 30) return "Willow";
        if (level >= 15) return "Oak";
        return "Tree";
    }
    
    private Area getTreeArea() {
        // Kept for internal logic if needed, but WalkingTask handles movement now. 
        // Can be removed if unused, but leaving for safety if other methods call it.
        // Or better, just remove usage above.
        // Actually, the previous edit removed the call to getTreeArea() in the else block.
        // So we can technically remove this method or just leave it unused.
        return null; 
    }

    @Override
    public String status() {
        return "Woodcutting";
    }
}
