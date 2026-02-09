package com.allinone.skills.firemaking.nodes;

import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.framework.AntiBan;
import com.allinone.framework.Blackboard;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.methods.walking.impl.Walking;

public class BurnLogNode extends LeafNode {

    private final Blackboard blackboard;

    public BurnLogNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        if (!Inventory.contains("Tinderbox")) {
            return Status.FAILURE; // Can't burn
        }
        
        // Robust Log Finding
        Item log = Inventory.get(i -> i != null && i.getName() != null && i.getName().toLowerCase().contains("logs"));
        
        if (log == null) {
             // Maybe we have "Logs" exactly or some other variation not caught?
             // Should not happen with toLowerCase().contains("logs")
             return Status.FAILURE;
        }
        
        // Ensure we are not standing on a fire or valid object that blocks fire
        Tile myTile = Players.getLocal().getTile();
        // Check for Fire or specified interruptive objects
        GameObject sameTileObj = GameObjects.closest(g -> g.getTile().equals(myTile) && (g.getName().equals("Fire") || g.getName().equals("Daisy") || g.getName().contains("Femur"))); 
        
        if (sameTileObj != null) {
            log("Current tile blocked by: " + sameTileObj.getName() + ". Moving away.");
            
            // Try to find a free tile in 4 directions
            Tile[] candidates = {
                myTile.translate(-1, 0), // West
                myTile.translate(1, 0),  // East
                myTile.translate(0, -1), // South
                myTile.translate(0, 1)   // North
            };
            
            for (Tile target : candidates) {
                if (Walking.canWalk(target) && !isTileBlocked(target)) {
                     Walking.walkExact(target);
                     Sleep.sleepUntil(() -> !Players.getLocal().isMoving() && !Players.getLocal().getTile().equals(myTile), 3000);
                     return Status.RUNNING;
                }
            }
            
            // If all blocked, just try West anyway (default fallback)
            Walking.walkExact(myTile.translate(-1, 0));
            return Status.RUNNING;
        }

        // Check if destination (West) is blocked by Collision or Objects
        Tile westTile = myTile.translate(-1, 0);
        // We typically move West. If we can't walk there, we shouldn't burn here.
        // isTileBlocked checks for Fire/Daisy/Femur, but we also want to check Walls/Decorations which Walking.canWalk checks (?)
        // Walking.canWalk checks if we can move FROM current TO target.
        if (!Walking.canWalk(westTile) || isTileBlocked(westTile)) {
             log("Cannot move West due to obstacle or fire/blocked tile. Relocating.");
             blackboard.setForceFiremakingRelocate(true);
             return Status.FAILURE;
        }

        if (log.useOn("Tinderbox")) {
             // Wait for animation to start
             Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 3000);
             
             // Wait for animation to finish
             Sleep.sleepUntil(() -> !Players.getLocal().isAnimating(), 10000);
             
             // Human-like sleep
             AntiBan.sleepStatic(500, 250); 
             return Status.RUNNING;
        }

        return Status.FAILURE;
    }

    private boolean isTileBlocked(Tile t) {
        // Use getObjectsOnTile for reliable checking
        GameObject[] objs = GameObjects.getObjectsOnTile(t);
        for (GameObject g : objs) {
            if (g != null && (g.getName().equals("Fire") || g.getName().equals("Daisy") || g.getName().contains("Femur"))) {
                return true;
            }
        }
        return false;
    }
}
