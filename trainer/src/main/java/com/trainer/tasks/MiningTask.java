package com.trainer.tasks;

import com.trainer.framework.Goal;
import com.trainer.framework.GoalManager;
import com.trainer.framework.Task;
import com.trainer.framework.Movement;
import com.trainer.data.TrainingArea;
import com.trainer.framework.InventoryManager;
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
import org.dreambot.api.methods.input.Camera;

public class MiningTask implements Task {
    private final AbstractScript script;
    private final GoalManager goalManager;
    private final StuckManager stuckManager = new StuckManager();
    private static final String[] ROCKS = {"Copper rocks", "Tin rocks", "Iron rocks"};
    
    // Areas handled by WalkingTask

    public MiningTask(AbstractScript script, GoalManager goalManager) {
        this.script = script;
        this.goalManager = goalManager;
    }

    @Override
    public boolean accept() {
        Goal goal = goalManager.getCurrentGoal();
        return goal != null && goal.getSkill() == Skill.MINING;
    }

    @Override
    public int execute() {
        if (Inventory.isFull()) {
            Goal goal = goalManager.getCurrentGoal();
            if (goal != null && goal.getMode() == Goal.TrainingMode.DROP) {
                script.log("Inventory full, dropping ore.");
                Inventory.dropAll(i -> i.getName().endsWith("ore"));
                return Calculations.random(1000, 2000);
            }
            return 1000; // Let BankTask handle it
        }

        if (!InventoryManager.prepare(script, goalManager)) {
            return 1000;
        }
        
        Goal goal = goalManager.getCurrentGoal();
        Area targetArea = TrainingArea.getArea(goal);
        if (targetArea != null && !targetArea.contains(Players.getLocal())) {
            script.log("Walking to Mining Area...");
            Movement.walkTo(targetArea);
            return 1000;
        }

        if (Players.getLocal().isAnimating()) {
            stuckManager.reset();
            return Calculations.random(500, 1000);
        }

        GameObject rock = GameObjects.closest(r -> {
            if (r == null || r.getName() == null) return false;
            if (stuckManager.isBlacklisted(r)) return false; 

            boolean nameMatch = false;
            for (String n : ROCKS) {
                if (r.getName().equals(n)) {
                    nameMatch = true;
                    break;
                }
            }
            return nameMatch && r.hasAction("Mine");
        });

        if (rock != null) {
            if (!rock.isOnScreen()) {
                Camera.rotateToEntity(rock);
            }
            if (rock.interact("Mine")) {
                if (script.sleepUntil(() -> Players.getLocal().isAnimating(), 5000, 100)) {
                    stuckManager.reset();
                    script.sleepUntil(() -> !Players.getLocal().isAnimating(), 10000, 500);
                } else {
                    stuckManager.trackFailure(rock);
                }
            }
        } 
        
        return Calculations.random(800, 1200);
    }

    @Override
    public String status() {
        return "Mining";
    }
}
