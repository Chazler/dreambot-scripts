package com.allinone.skills.mining.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;

public class MineRockNode extends LeafNode {

    private final Blackboard blackboard;
    private long lastActivityTime = System.currentTimeMillis();

    public MineRockNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        GameObject rock = blackboard.getCurrentObject();

        if (Players.getLocal().isAnimating()) {
            lastActivityTime = System.currentTimeMillis();
            // Check for competition while mining
            if (isOtherPlayerMining(rock)) {
                blackboard.setCurrentStatus("Competition detected! Finding new rock...");
                blackboard.setCurrentObject(null);
                return Status.FAILURE;
            }
            return Status.RUNNING;
        }

        if (rock == null || !rock.exists()) {
            lastActivityTime = System.currentTimeMillis();
            blackboard.setCurrentObject(null);
            return Status.FAILURE;
        }

        // Competition Check before mining
        if (isOtherPlayerMining(rock)) {
            blackboard.setCurrentObject(null);
            return Status.FAILURE;
        }

        // Safety check if rock was depleted (name changed to "Rocks" usually)
        if (blackboard.getCurrentMiningSpot() != null
                && rock.getName() != null
                && !rock.getName().equals(blackboard.getCurrentMiningSpot().getRockType().getObjectName())) {
            blackboard.setCurrentObject(null);
            return Status.FAILURE;
        }

        if (rock.distance(Players.getLocal()) > 10) {
            blackboard.setCurrentObject(null);
            return Status.FAILURE;
        }

        // Idle timeout: if we haven't been animating for 15s, something is wrong
        if (System.currentTimeMillis() - lastActivityTime > 15000) {
            log("Idle too long while trying to mine - resetting");
            blackboard.setCurrentObject(null);
            lastActivityTime = System.currentTimeMillis();
            return Status.FAILURE;
        }

        // Camera: rotate to face rock if it's not on screen
        if (!rock.isOnScreen()) {
            Camera.rotateToEntity(rock);
            blackboard.getAntiBan().sleep(400, 150);
        }

        if (rock.interact("Mine")) {
            blackboard.setCurrentStatus("Mining " + rock.getName());
            blackboard.getAntiBan().sleep(600, 250);

            if (Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 5000)) {
                lastActivityTime = System.currentTimeMillis();
                return Status.RUNNING;
            }
        }

        return Status.FAILURE;
    }

    private boolean isOtherPlayerMining(GameObject rock) {
        if (rock == null) return false;
        return !Players.all(p ->
            p != null &&
            !p.equals(Players.getLocal()) &&
            p.distance(rock) <= 1 &&
            p.isAnimating()
        ).isEmpty();
    }
}
