package com.allinone.skills.firemaking.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.framework.TravelHelper;
import org.dreambot.api.methods.interactive.Players;

import com.allinone.skills.firemaking.data.StaticBurnAreas;

public class TravelToBurnAreaNode extends LeafNode {

    private final Blackboard blackboard;

    public TravelToBurnAreaNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        StaticBurnAreas.BurnArea target = StaticBurnAreas.getNearest();
        if (target == null) {
            blackboard.setCurrentStatus("No burn areas found.");
            return Status.FAILURE;
        }

        if (target.getArea().contains(Players.getLocal())) {
            return Status.SUCCESS;
        }

        blackboard.setCurrentStatus("Walking to Burn Area: " + target.getName());
        TravelHelper.travelTo(target.getArea());

        return Status.RUNNING;
    }
}
