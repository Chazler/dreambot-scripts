package com.allinone.skills.magic.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.framework.TravelHelper;
import com.allinone.skills.magic.data.MagicTrainingLocation;
import com.allinone.skills.magic.strategies.MagicManager;
import org.dreambot.api.methods.interactive.Players;

public class TravelToMagicLocationNode extends LeafNode {

    private final Blackboard blackboard;

    public TravelToMagicLocationNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        MagicTrainingLocation loc = MagicManager.getBestLocation();
        if (loc == null) return Status.FAILURE;

        if (loc.getArea().contains(Players.getLocal())) {
            return Status.SUCCESS;
        }

        blackboard.setCurrentStatus("Traveling to " + loc.getName());
        TravelHelper.travelTo(loc.getArea());
        return Status.RUNNING;
    }
}
