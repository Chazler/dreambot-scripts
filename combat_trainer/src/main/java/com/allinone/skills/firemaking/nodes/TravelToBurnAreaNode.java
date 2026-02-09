package com.allinone.skills.firemaking.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.interactive.Players;
import com.allinone.framework.TravelHelper;
import com.allinone.skills.firemaking.data.StaticBurnAreas;

public class TravelToBurnAreaNode extends LeafNode {

    private final Blackboard blackboard;

    public TravelToBurnAreaNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        StaticBurnAreas.BurnArea target = StaticBurnAreas.getNearest();
        if (target == null) 
        {
            log("Couldn't travel to burn area.");
            blackboard.setCurrentStatus("No burn areas found.");
            return Status.FAILURE;
        }

        if (target.getArea().contains(Players.getLocal())) {
             return Status.SUCCESS;
        }

        if (Players.getLocal().isMoving()) {
            return Status.RUNNING;
        }
        
        log("Travelling to burn area");
        blackboard.setCurrentStatus("Walking to Burn Area: " + target.getName());
        TravelHelper.travelTo(target.getArea());
        
        return Status.RUNNING;
    }
}
