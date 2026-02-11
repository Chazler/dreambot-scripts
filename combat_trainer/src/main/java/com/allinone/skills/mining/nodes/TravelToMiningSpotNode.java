package com.allinone.skills.mining.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.framework.TravelHelper;
import com.allinone.skills.mining.data.MiningSpot;
import org.dreambot.api.methods.interactive.Players;

public class TravelToMiningSpotNode extends LeafNode {

    private final Blackboard blackboard;

    public TravelToMiningSpotNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        MiningSpot spot = blackboard.getCurrentMiningSpot();
        if (spot == null) return Status.FAILURE;

        if (spot.getArea().contains(Players.getLocal())) {
            return Status.SUCCESS;
        }

        blackboard.setCurrentStatus("Traveling to " + spot.getName());
        TravelHelper.travelTo(spot.getArea());

        return Status.RUNNING;
    }
}
