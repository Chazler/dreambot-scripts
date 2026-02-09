package com.allinone.skills.woodcutting.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.skills.woodcutting.data.StaticWoodcuttingSpots;
import com.allinone.skills.woodcutting.data.WoodcuttingSpot;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;

import com.allinone.skills.woodcutting.data.TreeType;
import org.dreambot.api.methods.interactive.Players;
import java.util.Comparator;
import java.util.List;

public class UpdateWoodcuttingStrategyNode extends LeafNode {

    private final Blackboard blackboard;

    public UpdateWoodcuttingStrategyNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        int level = Skills.getRealLevel(Skill.WOODCUTTING);
        
        // 1. Determine best tree type
        TreeType bestType = StaticWoodcuttingSpots.getBestTreeType(level);
        WoodcuttingSpot current = blackboard.getCurrentWoodcuttingSpot();
        
        // 2. If we already have a spot of the correct type, stick with it
        if (current != null && current.getTreeType() == bestType) {
            return Status.FAILURE;
        }
        
        // 3. Find closest spot of that type
        List<WoodcuttingSpot> candidates = StaticWoodcuttingSpots.getSpots(bestType);
        WoodcuttingSpot bestSpot = candidates.stream()
            .min(Comparator.comparingDouble(s -> s.getArea().getCenter().distance(Players.getLocal())))
            .orElse(null);
            
        // 4. Update if found
        if (bestSpot != null) {
            blackboard.setCurrentWoodcuttingSpot(bestSpot);
            log("Strategies Updated: New Spot -> " + bestSpot.getName() + " (Type: " + bestType + ")");
        }
        
        return Status.FAILURE;
    }
}
