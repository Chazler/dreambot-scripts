package com.combat_trainer.nodes;

import com.combat_trainer.data.LocationDef;
import com.combat_trainer.framework.Blackboard;
import com.combat_trainer.framework.LeafNode;
import com.combat_trainer.framework.Status;
import com.combat_trainer.strategies.LocationScorer;

public class UpdateStrategyNode extends LeafNode {
    private final Blackboard blackboard;

    public UpdateStrategyNode(Blackboard blackboard) {
        super();
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        // 1. Calculate best location
        LocationDef bestLoc = LocationScorer.getBestLocation();
        
        LocationDef current = blackboard.getBestLocation();
        if (current == null || !current.getName().equals(bestLoc.getName())) {
            log("New best location found: " + bestLoc.getName());
            blackboard.setBestLocation(bestLoc);
        }
        
        // 2. Calculate best gear (simplified for now)
        // List<GearItem> gear = GearScorer.getBestLoadout(getSkills());
        // blackboard.setDesiredGear(gear);
        
        return Status.SUCCESS; // We successfully updated state
    }
}
