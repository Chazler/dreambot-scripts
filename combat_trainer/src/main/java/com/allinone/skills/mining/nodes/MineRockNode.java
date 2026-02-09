package com.allinone.skills.mining.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Map;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;

public class MineRockNode extends LeafNode {

    private final Blackboard blackboard;

    public MineRockNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        GameObject rock = blackboard.getCurrentObject();
        
        if (Players.getLocal().isAnimating()) {
            // Check for competition while mining
            if (isOtherPlayerMining(rock)) {
                blackboard.setCurrentStatus("Competition detected! Finding new rock...");
                blackboard.setCurrentObject(null); // Force find new
                return Status.FAILURE;
            }
            return Status.RUNNING;
        }

        if (rock == null || !rock.exists()) {
             return Status.FAILURE;
        }
        
        // Competition Check before mining
        if (isOtherPlayerMining(rock)) {
             blackboard.setCurrentObject(null);
             return Status.FAILURE;
        }
        
        // Safety check if rock was depleted (name changed to "Rocks" usually)
        if (!rock.getName().equals(blackboard.getCurrentMiningSpot().getRockType().getObjectName())) {
             return Status.FAILURE;
        }

        if (rock.distance(Players.getLocal()) > 10) {
             // Too far
             return Status.FAILURE;
        }
        
        if (rock.interact("Mine")) {
            blackboard.setCurrentStatus("Mining " + rock.getName());
            
            blackboard.getAntiBan().sleep(600, 300);
            
            // Wait for mining to start
            if (Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 5000)) {
                
                // Wait while mining
                // Mining can be 1 tick or many. 
                // We return RUNNING if animating.
                // But we should probably loop here slightly to avoid spam clicking?
                // `execute` is called every tick. If we return RUNNING, the root selector 
                // re-evaluates. If we are animating, this node will be selected again (via Find->Mine sequence?)
                // Actually Sequence: Travel(Success) -> Find(Success) -> Mine(Running).
                // So next tick: Travel(Success) -> Find(Success) -> Mine(Running).
                // Find returns Success if object is set.
                
                return Status.RUNNING;
            }
        }

        return Status.FAILURE;
    }

    private boolean isOtherPlayerMining(GameObject rock) {
        if (rock == null) return false;
        // Check if any other player is mining this rock
        return !Players.all(p -> 
            p != null && 
            !p.equals(Players.getLocal()) && 
            p.distance(rock) <= 1 && 
            p.isAnimating() 
            // && p.getFacingDirection() ... roughly towards rock? 
            // distance <= 1 is usually "on top" or "adjacent". 
            // If they are adjacent and animating, likely mining.
        ).isEmpty();
    }
}
