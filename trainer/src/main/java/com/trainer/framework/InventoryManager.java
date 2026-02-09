package com.trainer.framework;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import java.util.Arrays;
import java.util.List;

public class InventoryManager {
    
    // Ordered Best-to-Worst for withdrawal priority
    private static final String[] PICKAXES = {"Rune pickaxe", "Adamant pickaxe", "Mithril pickaxe", "Steel pickaxe", "Iron pickaxe", "Bronze pickaxe"};
    private static final String[] AXES = {"Rune axe", "Adamant axe", "Mithril axe", "Steel axe", "Iron axe", "Bronze axe"};
    
    // Default fishing tools
    private static final String[] FISHING_TOOLS = {"Small fishing net", "Cage", "Harpoon", "Lobster pot"}; 

    public static boolean prepare(AbstractScript script, GoalManager goalManager) {
        Goal goal = goalManager.getCurrentGoal();
        if (goal == null) return true;
        
        String[] requiredItems = getRequiredItems(goal);
        boolean hasRequirement = checkHasRequirement(requiredItems);
        boolean hasJunk = containsJunk(goal);

        if (hasRequirement && !hasJunk) {
            return true;
        }
        
        script.log("InventoryManager checking state: Needed=" + Arrays.toString(requiredItems) + ", HasReq=" + hasRequirement + ", HasJunk=" + hasJunk);

        if (!Bank.isOpen()) {
             if (Bank.open()) { 
                 script.sleepUntil(() -> Bank.isOpen(), 2000, 50);
             } else {
                 return false;
             }
        }
        
        if (Bank.isOpen()) {
             if (hasJunk) {
                 script.log("Depositing Junk...");
                 Bank.depositAllExcept(i -> isItemAllowed(i, goal));
                 script.sleepUntil(() -> !containsJunk(goal), 2000, 50);
             }
             
             if (!hasRequirement) {
                 boolean found = false;
                 // Try to find best tool available
                 for (String item : requiredItems) {
                      if (Bank.contains(item)) {
                          script.log("Withdrawing " + item);
                          if (Bank.withdraw(item)) {
                               script.sleepUntil(() -> Inventory.contains(item), 3000, 50);
                          }
                          found = true;
                          break; 
                      }
                 }
                 
                 if (!found) {
                     script.log("No matching tools found in Bank! Deferring goal.");
                     goalManager.deferCurrentGoal();
                     Bank.close();
                     return false;
                 }
             }

             Bank.close();
             // Double check
             return checkHasRequirement(requiredItems) && !containsJunk(goal);
        }
        
        return false;
    }
    
    private static boolean checkHasRequirement(String[] items) {
        if (items.length == 0) return true;
        return Inventory.contains(items) || Equipment.contains(items);
    }

    private static String[] getRequiredItems(Goal goal) {
        switch (goal.getSkill()) {
            case WOODCUTTING:
                return AXES;
            case MINING:
                return PICKAXES;
            case FISHING:
                return FISHING_TOOLS;
            default:
                return new String[]{};
        }
    }

    private static boolean containsJunk(Goal goal) {
        return !Inventory.onlyContains(i -> isItemAllowed(i, goal));
    }
    
    private static boolean isItemAllowed(Item item, Goal goal) {
        if (item == null) return true;
        String name = item.getName().toLowerCase();
        
        if (name.contains("coin")) return true;
        
        switch (goal.getSkill()) {
            case WOODCUTTING:
                return name.contains("axe") || name.endsWith("log"); // Keep logs until full
            case MINING:
                return name.contains("pickaxe") || name.endsWith("ore");
            case FISHING:
                return name.contains("net") || name.contains("pot") || name.contains("cage") || name.contains("harpoon") || name.contains("raw");
            default:
                return true; 
        }
    }
}
