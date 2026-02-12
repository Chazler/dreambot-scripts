package com.allinone.skills.combat.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.GroundItem;

import java.util.Arrays;
import java.util.List;

public class LootNode extends LeafNode {

    private final Blackboard blackboard;
    private final List<String> LOOT_LIST = Arrays.asList("Big bones");

    public LootNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        if (Inventory.isFull()) {
            return Status.SUCCESS; // Move on if full (or FAILURE if we want to force banking?)
            // If we return SUCCESS, we skip looting and go to bury/fight.
        }

        if (Players.getLocal().isInCombat()) {
             return Status.SUCCESS; // Skip looting in combat
        }

        org.dreambot.api.methods.map.Tile killTile = blackboard.getLastKillTile();
        if (killTile == null || killTile.distance(Players.getLocal()) > 10) {
            // No recent kill or too far away to care
             return Status.SUCCESS;
        }
        
        GroundItem loot = GroundItems.closest(i -> 
            i != null && 
            i.getName() != null && 
            LOOT_LIST.contains(i.getName()) && 
            i.getTile().distance(killTile) <= 1 && // Only loot items on the kill tile (or strictly adjacent)
            i.distance(Players.getLocal()) < 10
        );

        if (loot != null) {
            // Optimization: Don't spam click if moving
            org.dreambot.api.methods.map.Tile dest = org.dreambot.api.Client.getDestination();
            if (Players.getLocal().isMoving() && dest != null &&
                loot.distance(dest) < 2) {
                 return Status.RUNNING;
            }

            if (loot.interact("Take")) {
                blackboard.setCurrentStatus("Looting " + loot.getName());
                Sleep.sleepUntil(() -> !loot.exists(), 3000); // Small sleep to register
                return Status.RUNNING; 
            }
        }

        return Status.SUCCESS; // Nothing to loot, proceed
    }
}
