package com.allinone.skills.fishing.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.skills.fishing.data.FishingSpot;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.wrappers.interactive.NPC;

public class FindFishNode extends LeafNode {

    private final Blackboard blackboard;

    public FindFishNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        FishingSpot spot = blackboard.getCurrentFishingSpot();
        if (spot == null) return Status.FAILURE;
        
        NPC fishSpot = NPCs.closest(n -> 
            n != null && 
            n.getName().equals(spot.getNpcName()) && 
            n.hasAction(spot.getMethod().getAction()) &&
            spot.getArea().contains(n)
        );
        
        if (fishSpot != null) {
            blackboard.setCurrentTarget(fishSpot); 
            // Reuse generic 'currentTarget' or add 'currentNpc'?
            // Blackboard uses NPC currentTarget, usually for combat.
            // Let's use it here too since it's an NPC wrapper, or use a new field if we want separation.
            // Using currentTarget might confuse Combat nodes if reused, but here we are in Fishing Skill.
            blackboard.setCurrentStatus("Found spot: " + fishSpot.getName());
            return Status.SUCCESS;
        }
        
        blackboard.setCurrentStatus("Searching for fishing spot...");
        return Status.FAILURE;
    }
}
