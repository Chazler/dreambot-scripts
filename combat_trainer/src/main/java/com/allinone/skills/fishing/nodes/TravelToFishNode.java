package com.allinone.skills.fishing.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.skills.fishing.data.FishingSpot;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.interactive.Players;

import com.allinone.framework.TravelHelper;

public class TravelToFishNode extends LeafNode {

    private final Blackboard blackboard;

    public TravelToFishNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        FishingSpot spot = blackboard.getCurrentFishingSpot();
        if (spot == null) return Status.FAILURE;
        
        if (Inventory.isFull()) return Status.FAILURE; // Don't travel if full (Drop/Bank handles this)
        
        if (spot.getArea().contains(Players.getLocal())) {
            return Status.SUCCESS;
        }
        
        blackboard.setCurrentStatus("Traveling to Fishing Spot");
        TravelHelper.travelTo(spot.getArea());
        
        return Status.RUNNING;
    }
}
