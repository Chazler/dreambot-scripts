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
        NPC target = blackboard.getCurrentTarget();
        
        // Check if target died recently while we were tracking it
        if (target != null && !target.exists() && target.getHealthPercent() == 0) {
             // Target likely died. (However, !exists() means obj is gone from client memory or far away)
             // Better way: Check if we were interacting with it and it disappeared or died.
             // If we just killed it, we should update last kill tile.
        }

        if (Players.getLocal().isInCombat()) {
            blackboard.setCurrentStatus("In Combat");
            
            // While in combat, keep track of the target's tile just before it dies
            if (Players.getLocal().getInteractingCharacter() != null) {
                // If we are interacting with an NPC, update our potential kill tile
                blackboard.setLastKillTile(Players.getLocal().getInteractingCharacter().getTile());
            }

            // Perform random anti-ban actions during combat like checking stats or moving mouse
            blackboard.getAntiBan().performTimedAntiBan();
            return Status.SUCCESS; 
        }

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
