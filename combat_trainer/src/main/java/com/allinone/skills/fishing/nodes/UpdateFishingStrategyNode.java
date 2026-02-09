package com.allinone.skills.fishing.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.skills.fishing.data.FishingSpot;
import com.allinone.skills.fishing.data.StaticFishingSpots;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;

public class UpdateFishingStrategyNode extends LeafNode {

    private final Blackboard blackboard;

    public UpdateFishingStrategyNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        int level = Skills.getRealLevel(Skill.FISHING);
        FishingSpot best = StaticFishingSpots.getBestSpot(level);
        
        FishingSpot current = blackboard.getCurrentFishingSpot();
        
        if (current == null || !current.getNpcName().equals(best.getNpcName()) || !current.getMethod().equals(best.getMethod())) {
            blackboard.setCurrentFishingSpot(best);
            log("Fishing Strategy Updated: " + best.getNpcName() + " (" + best.getMethod().name() + ") at " + best.getArea().getCenter());
        }
        
        return Status.FAILURE;
    }
}
