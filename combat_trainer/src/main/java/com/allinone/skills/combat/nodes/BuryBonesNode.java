package com.allinone.skills.combat.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.methods.interactive.Players;

public class BuryBonesNode extends LeafNode {

    private final Blackboard blackboard;

    public BuryBonesNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        if (Players.getLocal().isInCombat()) {
            return Status.SUCCESS; // Move on
        }

        Item bones = Inventory.get(i -> 
            i != null && 
            i.getName() != null && 
            (i.getName().equals("Big bones") || i.getName().equals("Bones"))
        );

        if (bones != null) {
            blackboard.setCurrentStatus("Burying " + bones.getName());
            if (bones.interact("Bury")) {
                Sleep.sleepUntil(() -> !bones.isValid(), 1500);
                return Status.RUNNING;
            }
        }

        return Status.SUCCESS; // No bones to bury, proceed
    }
}
