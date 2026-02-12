package com.allinone.skills.woodcutting.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.wrappers.interactive.GameObject;

import com.allinone.skills.woodcutting.data.WoodcuttingSpot;

public class FindTreeNode extends LeafNode {

    private final Blackboard blackboard;

    public FindTreeNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        WoodcuttingSpot spot = blackboard.getCurrentWoodcuttingSpot();
        if (spot == null) return Status.FAILURE;
        
        // If we already have a valid tree, reuse it
        GameObject existing = blackboard.getCurrentObject();
        if (existing != null && existing.exists()
                && existing.getName() != null
                && (existing.getName().equals(spot.getTreeType().getTreeName()) || existing.getName().equals(spot.getTreeType().getTreeName() + " tree"))
                && spot.getArea().contains(existing)) {
            return Status.SUCCESS;
        }

        // Find tree within the defined area
        GameObject tree = GameObjects.closest(t ->
            t != null &&
            t.getName() != null &&
            (t.getName().equals(spot.getTreeType().getTreeName()) || t.getName().equals(spot.getTreeType().getTreeName() + " tree")) &&
            spot.getArea().contains(t) &&
            t.hasAction("Chop down")
        );

        if (tree != null) {
            blackboard.setCurrentObject(tree);
            blackboard.setCurrentStatus("Found tree: " + tree.getName());
            blackboard.getAntiBan().sleep(600, 250);
            return Status.SUCCESS;
        }
        
        blackboard.setCurrentStatus("Searching for " + spot.getTreeType().getTreeName() + "...");
        return Status.FAILURE;
    }
}
