package com.allinone.skills.woodcutting.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.skills.woodcutting.data.StaticWoodcuttingSpots;
import com.allinone.skills.woodcutting.data.WoodcuttingSpot;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;

public class UpdateWoodcuttingStrategyNode extends LeafNode {

    private final Blackboard blackboard;

    public UpdateWoodcuttingStrategyNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        // Re-evaluate best spot based on level
        int level = Skills.getRealLevel(Skill.WOODCUTTING);
        WoodcuttingSpot best = StaticWoodcuttingSpots.getBestSpot(level);
        
        WoodcuttingSpot current = blackboard.getCurrentWoodcuttingSpot();
        
        if (current == null || !current.getName().equals(best.getName())) {
            blackboard.setCurrentWoodcuttingSpot(best);
            log("Strategies Updated: New Spot -> " + best.getName());
        }
        
        // Return FAILURE so the Selector continues to the next node (Logic)
        return Status.FAILURE;
    }
}
