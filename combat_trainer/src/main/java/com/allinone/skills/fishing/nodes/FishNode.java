package com.allinone.skills.fishing.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.skills.fishing.data.FishingSpot;
import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;

public class FishNode extends LeafNode {

    private final Blackboard blackboard;
    private long lastActivityTime = System.currentTimeMillis();

    public FishNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        NPC spot = blackboard.getCurrentTarget();
        FishingSpot def = blackboard.getCurrentFishingSpot();

        // Already fishing - track activity
        if (Players.getLocal().getAnimation() != -1) {
            lastActivityTime = System.currentTimeMillis();
            return Status.RUNNING;
        }

        if (spot == null || !spot.exists() || def == null) {
            lastActivityTime = System.currentTimeMillis();
            return Status.FAILURE;
        }

        // Unreachable spot - clear and let find node pick a new one
        if (!spot.canReach()) {
            log("Spot unreachable - clearing target");
            blackboard.setCurrentTarget(null);
            return Status.FAILURE;
        }

        // Idle timeout: if we haven't been fishing for 15s, something is wrong
        if (System.currentTimeMillis() - lastActivityTime > 15000) {
            log("Idle too long while trying to fish - resetting");
            blackboard.setCurrentTarget(null);
            lastActivityTime = System.currentTimeMillis();
            return Status.FAILURE;
        }

        // Camera: rotate to face spot if it's not on screen
        if (!spot.isOnScreen()) {
            Camera.rotateToEntity(spot);
            blackboard.getAntiBan().sleep(400, 150);
        }

        if (spot.interact(def.getMethod().getAction())) {
            blackboard.setCurrentStatus("Fishing...");
            blackboard.getAntiBan().sleep(600, 200);
            Sleep.sleepUntil(() -> Players.getLocal().getAnimation() != -1, 5000);
            if (Players.getLocal().getAnimation() != -1) {
                lastActivityTime = System.currentTimeMillis();
            }
            return Status.RUNNING;
        }

        return Status.FAILURE;
    }
}
