package com.combat_trainer.nodes;

import com.combat_trainer.data.LocationDef;
import com.combat_trainer.framework.Blackboard;
import com.combat_trainer.framework.LeafNode;
import com.combat_trainer.framework.Status;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.utilities.Sleep;

public class TravelNode extends LeafNode {
    private final Blackboard blackboard;
    private Tile activeDestination = null;
    private int stuckTicks = 0;
    private Tile lastPlayerLoc = null;

    public TravelNode(Blackboard blackboard) {
        super();
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        LocationDef targetLoc = blackboard.getBestLocation();
        if (targetLoc == null) {
            log("No target location set!");
            return Status.FAILURE;
        }
        
        Tile localTile = Players.getLocal().getTile(); // Cache for logging
        
        if (targetLoc.getArea().contains(Players.getLocal())) {
            // We are there
            activeDestination = null;
            stuckTicks = 0;
            return Status.SUCCESS;
        }

        // If we are performing an animation (like climbing stairs), wait
        boolean isAnimating = Players.getLocal().isAnimating();
        if (isAnimating) {
            log("[Travel] Animating (Stairs/Interact). Waiting...");
            return Status.RUNNING;
        }

        // Stuck detection
        Tile currentLoc = Players.getLocal().getTile();
        boolean isMoving = Players.getLocal().isMoving();
        
        if (lastPlayerLoc != null && currentLoc.equals(lastPlayerLoc) && !isMoving) {
            stuckTicks++;
            if (stuckTicks % 5 == 0) log("[Travel] Stuck count: " + stuckTicks + " at " + currentLoc);
        } else {
            stuckTicks = 0;
            lastPlayerLoc = currentLoc;
        }

        // If stuck for ~6 seconds (10 ticks), reset destination
        if (stuckTicks > 10) {
            log("[Travel] Stuck detected! (>10 ticks static). Resetting destination.");
            activeDestination = null;
            stuckTicks = 0;
        }

        // Stabilize destination: pick one tile and stick to it
        if (activeDestination == null || !targetLoc.getArea().contains(activeDestination)) {
            activeDestination = targetLoc.getArea().getRandomTile();
            log("[Travel] New active destination: " + activeDestination);
        }
        
        // Only click if we aren't already moving to a valid destination or if we are idle
        if (Walking.shouldWalk(6)) {
            String msg = "Traveling to " + targetLoc.getName() + " [Dest: " + activeDestination + "]";
            log(msg);
            blackboard.setCurrentStatus(msg);
            
            // Random anti-ban while walking
            blackboard.getAntiBan().performTimedAntiBan();

            // Walk to the stable cached tile
            log("[Travel] Walking.walk(" + activeDestination + ")...");
            if (Walking.walk(activeDestination)) {
                log("[Travel] Walk action successful. Sleeping for movement...");
                
                // IMPORTANT: Wait for movement to actually start or sleep to prevent spam
                Sleep.sleepUntil(() -> Players.getLocal().isMoving() || Players.getLocal().isAnimating(), 2000);
                
                // Add human-like post-click delay
                blackboard.getAntiBan().sleep(600, 200);
            } else {
                log("[Travel] Walking.walk returned FALSE for " + activeDestination);
                activeDestination = null; // Force new pick
            }
        } else {
             // We are moving appropriately
             // log("[Travel] Moving..."); // Verbose
             blackboard.getAntiBan().performTimedAntiBan();
        }
        
        // Return RUNNING so we don't break the sequence and reset targets mechanism
        // properly wait until we arrive.
        return Status.RUNNING;
    }
}
