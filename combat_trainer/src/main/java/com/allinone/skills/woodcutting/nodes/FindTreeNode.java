package com.allinone.skills.woodcutting.nodes;

import com.allinone.framework.AntiBan;
import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.skills.woodcutting.data.TreeType;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
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
        
        // Find tree within the defined area
        GameObject tree = GameObjects.closest(t -> 
            t != null && 
            t.getName().equals(spot.getTreeType().getTreeName()) &&
            spot.getArea().contains(t)
        );
        
        if (tree != null) {
            blackboard.setCurrentObject(tree);
            blackboard.setCurrentStatus("Found tree: " + tree.getName());
            AntiBan.sleepStatic(800, 400); // Short pause before next action
            return Status.SUCCESS;
        }
        
        blackboard.setCurrentStatus("Searching for " + spot.getTreeType().getTreeName() + "...");
        return Status.FAILURE;
    }
}
