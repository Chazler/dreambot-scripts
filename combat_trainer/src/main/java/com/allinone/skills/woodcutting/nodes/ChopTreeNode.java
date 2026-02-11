package com.allinone.skills.woodcutting.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;

public class ChopTreeNode extends LeafNode {

    private final Blackboard blackboard;
    private long lastActivityTime = System.currentTimeMillis();

    public ChopTreeNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        GameObject tree = blackboard.getCurrentObject();

        // If animating, we're actively chopping - track activity
        if (Players.getLocal().isAnimating()) {
            lastActivityTime = System.currentTimeMillis();
            return Status.RUNNING;
        }

        if (tree == null || !tree.exists()) {
            lastActivityTime = System.currentTimeMillis();
            return Status.FAILURE;
        }

        // If tree is too far, clear and let find node pick a new one
        if (tree.distance(Players.getLocal()) > 10) {
            blackboard.setCurrentObject(null);
            return Status.FAILURE;
        }

        // Idle timeout: if we haven't been animating for 15s, something is wrong
        if (System.currentTimeMillis() - lastActivityTime > 15000) {
            log("Idle too long while trying to chop - resetting");
            blackboard.setCurrentObject(null);
            lastActivityTime = System.currentTimeMillis();
            return Status.FAILURE;
        }

        // Camera: rotate to face tree if it's not on screen
        if (!tree.isOnScreen()) {
            Camera.rotateToEntity(tree);
            blackboard.getAntiBan().sleep(400, 150);
        }

        if (tree.interact("Chop down")) {
            blackboard.setCurrentStatus("Chopping " + tree.getName());
            // Human-like: wait for animation to start with a reaction delay
            blackboard.getAntiBan().sleep(800, 300);
            Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 5000);
            if (Players.getLocal().isAnimating()) {
                lastActivityTime = System.currentTimeMillis();
            }
            return Status.RUNNING;
        }

        return Status.FAILURE;
    }
}
