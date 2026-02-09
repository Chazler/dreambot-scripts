package com.trainer.tasks;

import com.trainer.framework.Task;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.methods.input.Camera;

import org.dreambot.api.script.AbstractScript;

public class LootTask implements Task {
    private static final int MIN_VALUE = 1000; // Minimum value to loot
    private static final int MAX_DISTANCE = 3; // Maximum distance to consider "my drop"
    private final AbstractScript script;

    public LootTask(AbstractScript script) {
        this.script = script;
    }


    @Override
    public boolean accept() {
        if (Players.getLocal().isInCombat()) return false;
        
        // Find loot that matches our criteria
        return getLoot() != null;
    }

    @Override
    public int execute() {
        GroundItem loot = getLoot();
        if (loot != null) {
            String lootName = loot.getName();
            if (!loot.isOnScreen()) {
                Camera.rotateToEntity(loot);
            }
            if (loot.interact("Take")) {
                // Wait a bit for pickup - loop until item is gone or logic breaks
                script.sleepUntil(() -> !loot.exists(), 3000, 100); 
                return Calculations.random(1000, 1500);
            }
        }
        return Calculations.random(600, 1000);
    }

    private GroundItem getLoot() {
        return GroundItems.closest(item -> {
            if (item == null || !item.exists()) return false;
            
            // "Drop is yours" heuristic: Check distance. 
            // Usually drops appear under the NPC we just killed (nearby).
            if (item.distance(Players.getLocal()) > MAX_DISTANCE) return false;

            // Check value
            int value = LivePrices.getHigh(item.getID()); // Gets price for 1 item
            long totalValue = (long) value * item.getAmount();
            
            return totalValue >= MIN_VALUE;
        });
    }

    @Override
    public String status() {
        return "Looting";
    }
}
