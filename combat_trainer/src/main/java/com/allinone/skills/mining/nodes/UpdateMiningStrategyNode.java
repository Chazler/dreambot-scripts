package com.allinone.skills.mining.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.skills.mining.data.MiningSpot;
import com.allinone.skills.mining.data.RockType;
import com.allinone.skills.mining.data.StaticMiningSpots;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;

import java.util.List;

public class UpdateMiningStrategyNode extends LeafNode {

    private final Blackboard blackboard;

    public UpdateMiningStrategyNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        // If we already have a suitable spot, keep it? 
        // Or re-evaluate every time? 
        // For simplicity, let's re-evaluate if null, or if we outlevelled it?
        // Actually, Woodcutting re-evaluates always. Let's do that but be efficient.
        
        int level = Skills.getRealLevel(Skill.MINING);
        RockType bestType = StaticMiningSpots.getBestRockType(level);
        
        MiningSpot current = blackboard.getCurrentMiningSpot();
        
        if (current != null && current.getRockType() == bestType) {
            // We are already on the best target type. Pass through to next node.
            return Status.FAILURE;
        }

        // Find a spot for this rock type
        List<MiningSpot> spots = StaticMiningSpots.getSpots(bestType);
        if (spots.isEmpty()) {
            Logger.log("No mining spot found for " + bestType);
            return Status.FAILURE;
        }

        // Just pick the first one for now, or random?
        MiningSpot bestSpot = spots.get(0);
        
        if (current == null || !current.equals(bestSpot)) {
            Logger.log("Updating Strategy: Level " + level + " -> " + bestType.name() + " at " + bestSpot.getName());
            blackboard.setCurrentMiningSpot(bestSpot);
        }
        
        // Always return FAILURE so the Selector continues to the next node (pickaxe check, banking, mining)
        return Status.FAILURE;
    }
}
