package com.allinone.skills.ranged.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.skills.ranged.data.RangedTrainingLocation;
import com.allinone.skills.ranged.data.StaticRangedLocations;

public class UpdateRangedStrategyNode extends LeafNode {

    private final Blackboard blackboard;
    private RangedTrainingLocation lastLocation;

    public UpdateRangedStrategyNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        RangedTrainingLocation best = StaticRangedLocations.getBestLocation();

        if (best != lastLocation) {
            lastLocation = best;
            blackboard.setCurrentRangedLocation(best);
            log("Ranged Strategy Updated: " + best.getName());
        }

        return Status.FAILURE; // Selector: pass to next child
    }
}
