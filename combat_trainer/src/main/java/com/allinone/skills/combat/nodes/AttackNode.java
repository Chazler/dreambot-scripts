package com.allinone.skills.combat.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.wrappers.interactive.NPC;

public class AttackNode extends LeafNode {
    private final Blackboard blackboard;

    public AttackNode(Blackboard blackboard) {
        super();
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        if (Players.getLocal().isInCombat()) {
            blackboard.setCurrentStatus("In Combat");
            // Perform random anti-ban actions during combat like checking stats or moving mouse
            blackboard.getAntiBan().performTimedAntiBan();
            return Status.SUCCESS; 
        }

        NPC target = blackboard.getCurrentTarget();
        if (target == null || !target.exists() || target.distance(Players.getLocal()) > 15) {
            return Status.FAILURE; // Need to go back to AcquireTarget
        }
        
        if (target.interact("Attack")) {
            String msg = "Attacking " + target.getName();
            log(msg);
            blackboard.setCurrentStatus(msg);
            // We successfully performed the action. 
            // The tree will likely tick again next loop.
            return Status.RUNNING; // Or SUCCESS, depending on if we wait for combat start. 
                                  // RUNNING implies we are "busy attacking".
        }
        
        return Status.FAILURE;
    }
}
