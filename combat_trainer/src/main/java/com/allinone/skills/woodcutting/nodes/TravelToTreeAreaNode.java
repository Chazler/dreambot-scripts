package com.allinone.skills.woodcutting.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.skills.woodcutting.data.WoodcuttingSpot;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.map.Map;
import org.dreambot.api.methods.walking.impl.Walking;
import com.allinone.framework.TravelHelper;
import org.dreambot.api.methods.interactive.Players;

public class TravelToTreeAreaNode extends LeafNode {

    private final Blackboard blackboard;

    public TravelToTreeAreaNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        WoodcuttingSpot spot = blackboard.getCurrentWoodcuttingSpot();
        if (spot == null) return Status.FAILURE;
        
        // If inventory is full, we don't travel to tree area, we go to bank (handled by BankLogsNode priority)
        if (Inventory.isFull()) return Status.FAILURE; 
        
        if (spot.getArea().contains(Players.getLocal())) {
            return Status.SUCCESS;
        }
        
        blackboard.setCurrentStatus("Traveling to " + spot.getName());
        TravelHelper.travelTo(spot.getArea());
        
        return Status.RUNNING;
    }
}
