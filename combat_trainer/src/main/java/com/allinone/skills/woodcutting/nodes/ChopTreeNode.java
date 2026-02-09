package com.allinone.skills.woodcutting.nodes;

import com.allinone.framework.AntiBan;
import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Map;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;

public class ChopTreeNode extends LeafNode {

    private final Blackboard blackboard;

    public ChopTreeNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        GameObject tree = blackboard.getCurrentObject();
        
        if (Players.getLocal().isAnimating()) {
            return Status.RUNNING;
        }

        if (tree == null || !tree.exists()) {
             return Status.FAILURE;
        }
        
        // Ensure tree is still the same one (ids can match but instance difference?)
        // Dreambot wrappers usually handle this, but distance check is good.
        if (tree.distance(Players.getLocal()) > 10) {
             // Maybe walk to it? Or just fail and let find node run again?
             // Walking should ideally be handled by a WalkToNode, but we keep it simple here.
             if (Map.canReach(tree)) { // Simplified reach check
                 // It's reachable but far?
             }
        }
        
        if (tree.interact("Chop down")) {
            blackboard.setCurrentStatus("Chopping " + tree.getName());
            // Human-like: Wait a bit after clicking before doing anything else
            blackboard.getAntiBan().sleep(900, 300);
            blackboard.getAntiBan().sleepUntil(() -> Players.getLocal().isAnimating(), 5000);
            return Status.RUNNING;
        }

        return Status.FAILURE;
    }
}
