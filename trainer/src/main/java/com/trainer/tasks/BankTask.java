package com.trainer.tasks;

import com.trainer.framework.Goal;
import com.trainer.framework.GoalManager;
import com.trainer.framework.Task;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.script.AbstractScript;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.wrappers.items.Item;

public class BankTask implements Task {
    private final AbstractScript script;
    private final GoalManager goalManager;

    public BankTask(AbstractScript script, GoalManager goalManager) {
        this.script = script;
        this.goalManager = goalManager;
    }

    @Override
    public boolean accept() {
        Goal goal = goalManager.getCurrentGoal();
        
        // 1. Bank if Inventory is full (and mode is BANK)
        if (Inventory.isFull() && goal != null && goal.getMode() == Goal.TrainingMode.BANK) {
            return true;
        }

        return false;
    }

    private boolean hasJunkItems(Goal goal) {
        // If we are at the bank, we always handle this. But we don't want to walk to the bank just for one junk item?
        // User request implies strict inventory management.
        return !Inventory.onlyContains(i -> isItemAllowed(i, goal));
    }
    
    private boolean isItemAllowed(Item item, Goal goal) {
        if (item == null) return true;
        String name = item.getName().toLowerCase();
        
        // Always allowed
        if (name.contains("coin")) return true;
        if (name.contains("rune")) return true; // Teleports
        if (name.contains("tab")) return true; // Tabs
        
        switch (goal.getSkill()) {
            case WOODCUTTING:
                return name.contains("axe");
            case MINING:
                return name.contains("pickaxe");
            case FISHING:
                return name.contains("net") || name.contains("pot") || name.contains("cage") || name.contains("harpoon");
            default:
                // Combat: allow food, potions, gear
                return true; 
        }
    }

    @Override
    public int execute() {
        script.log("Banking items...");
        
        if (!Bank.open()) {
            return Calculations.random(1000, 2000); 
        }
        
        if (Bank.isOpen()) {
            Goal goal = goalManager.getCurrentGoal();
            if (goal != null) {
                // Deposit items not needed for current goal
                 Bank.depositAllExcept(i -> isItemAllowed(i, goal));
            } else {
                 Bank.depositAllItems(); // No goal? Clean slate but depositAll() isn't valid no-arg
            }

            script.sleepUntil(() -> !Inventory.isFull(), 3000, 100);
            
            if (!Inventory.isFull()) {
                Bank.close();
                return Calculations.random(800, 1200);
            }
        }
        
        return 1000;
    }

    @Override
    public String status() {
        return "Banking";
    }
}
