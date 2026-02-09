package com.allinone.skills.combat.nodes;

import com.allinone.skills.combat.data.LocationDef;
import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.skills.combat.strategies.LocationScorer;

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
