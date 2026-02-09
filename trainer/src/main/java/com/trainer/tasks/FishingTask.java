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
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.input.Camera;

public class FishingTask implements Task {
    private final AbstractScript script;
    private final GoalManager goalManager;
    private final StuckManager stuckManager = new StuckManager();
    // Area handled by WalkingTask
    
    // Simple basic fishing tools/actions
    private static final String NET = "Small fishing net";
    private static final String CAGE = "Lobster pot";
    
    public FishingTask(AbstractScript script, GoalManager goalManager) {
        this.script = script;
        this.goalManager = goalManager;
    }

    @Override
    public boolean accept() {
        Goal goal = goalManager.getCurrentGoal();
        return goal != null && goal.getSkill() == Skill.FISHING;
    }

    @Override
    public int execute() {
        if (Inventory.isFull()) {
            Goal goal = goalManager.getCurrentGoal();
            if (goal != null && goal.getMode() == Goal.TrainingMode.DROP) {
                script.log("Inventory full, dropping fish.");
                Inventory.dropAll(i -> i.getName().startsWith("Raw") || i.getName().equals("Shrimps") || i.getName().equals("Anchovies"));
                return Calculations.random(1000, 2000);
            }
            return 1000; // Let BankTask handle it
        }

        // Ensure tool logic
        String action = null;
        String spotName = "Fishing spot";

        if (!InventoryManager.prepare(script, goalManager)) {
            return 1000;
        }
        
        // Decide action based on what we have
        if (Inventory.contains(NET)) {
            action = "Small Net";
        } else if (Inventory.contains(CAGE)) {
            action = "Cage";
        } else if (Inventory.contains("Harpoon")) {
            action = "Harpoon";
        }
        
        // Centralized Area Check
        Goal goal = goalManager.getCurrentGoal();
        Area targetArea = TrainingArea.getArea(goal);
        if (targetArea != null && !targetArea.contains(Players.getLocal())) {
            script.log("Walking to Fishing Area...");
            Movement.walkTo(targetArea);
            return 1000;
        }

        if (Players.getLocal().isAnimating()) {
            stuckManager.reset();
            return Calculations.random(1000, 2000);
        }

        String finalAction = action;
        NPC spot = NPCs.closest(n -> n.getName().equals(spotName) && n.hasAction(finalAction) && !stuckManager.isBlacklisted(n));
        
        if (spot != null) {
            if (!spot.isOnScreen()) {
                Camera.rotateToEntity(spot);
            }
            if (spot.interact(finalAction)) {
                if (script.sleepUntil(() -> Players.getLocal().isAnimating(), 5000, 100)) {
                    stuckManager.reset();
                    script.sleepUntil(() -> !Players.getLocal().isAnimating() || Inventory.isFull(), 15000, 1000);
                } else {
                    stuckManager.trackFailure(spot);
                }
            }
        } else {
            script.log("No matching action found for " + action + " (WalkingTask should handle positioning)");
        }

        return Calculations.random(800, 1200);
    }
    
    @Override
    public String status() {
        return "Fishing";
    }
}
