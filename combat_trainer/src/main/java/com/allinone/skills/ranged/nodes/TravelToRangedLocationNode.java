package com.allinone.skills.ranged.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.framework.TravelHelper;
import com.allinone.skills.ranged.data.RangedTrainingLocation;
import org.dreambot.api.methods.interactive.Players;

public class TravelToRangedLocationNode extends LeafNode {

    private final Blackboard blackboard;

    public TravelToRangedLocationNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        RangedTrainingLocation loc = blackboard.getCurrentRangedLocation();
        if (loc == null) return Status.FAILURE;

        if (loc.getArea().contains(Players.getLocal())) {
            return Status.SUCCESS;
        }

        blackboard.setCurrentStatus("Traveling to " + loc.getName());
        TravelHelper.travelTo(loc.getArea());
        return Status.RUNNING;
    }
}
