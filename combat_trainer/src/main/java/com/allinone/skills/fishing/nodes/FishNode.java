package com.allinone.skills.fishing.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.skills.fishing.data.FishingSpot;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;

public class FishNode extends LeafNode {

    private final Blackboard blackboard;

    public FishNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        NPC spot = blackboard.getCurrentTarget();
        FishingSpot def = blackboard.getCurrentFishingSpot();
        
        if (Players.getLocal().isAnimating()) {
            return Status.RUNNING;
        }

        if (spot == null || !spot.exists() || def == null) {
             return Status.FAILURE;
        }
        
        if (spot.interact(def.getMethod().getAction())) {
            blackboard.setCurrentStatus("Fishing...");
            Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 3000);
            return Status.RUNNING;
        }

        return Status.FAILURE;
    }
}
