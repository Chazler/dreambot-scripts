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
        
        // Double check we are not already fishing
        // Animation 621, 622, 623 etc are fishing. -1 is idle.
        if (Players.getLocal().getAnimation() != -1) {
            return Status.RUNNING;
        }

        if (spot == null || !spot.exists() || def == null) {
            // Try to find it again? No, let Sequence fail so FindFishNode runs again
            return Status.FAILURE;
        }
        
        if (!spot.canReach()) {
            // Should be handled by travel, but if we are close, walk to it?
            // Actually interact usually handles short distance.
            log("Spot unreachable?");
        }

        if (spot.interact(def.getMethod().getAction())) {
            blackboard.setCurrentStatus("Fishing...");
            Sleep.sleepUntil(() -> Players.getLocal().getAnimation() != -1, 4000);
            return Status.RUNNING;
        }

        return Status.FAILURE;
    }
}
