package com.allinone.skills.ranged.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;

public class RangedAttackNode extends LeafNode {

    private final Blackboard blackboard;

    public RangedAttackNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        if (Players.getLocal().isInCombat()) {
            blackboard.setCurrentStatus("In Ranged Combat");
            blackboard.getAntiBan().performTimedAntiBan();
            return Status.SUCCESS;
        }

        NPC target = blackboard.getCurrentTarget();
        if (target == null || !target.exists() || target.getHealthPercent() <= 0) {
            blackboard.setCurrentTarget(null);
            return Status.FAILURE;
        }

        if (target.distance(Players.getLocal()) > 15) {
            return Status.FAILURE;
        }

        // Rotate camera to target if not on screen
        if (!target.isOnScreen()) {
            Camera.rotateToEntity(target);
            blackboard.getAntiBan().sleep(400, 150);
        }

        if (target.interact("Attack")) {
            String msg = "Ranging " + target.getName();
            log(msg);
            blackboard.setCurrentStatus(msg);
            blackboard.getAntiBan().sleep(600, 200);
            Sleep.sleepUntil(() -> Players.getLocal().isInCombat(), 3000);
            return Status.RUNNING;
        }

        return Status.FAILURE;
    }
}
