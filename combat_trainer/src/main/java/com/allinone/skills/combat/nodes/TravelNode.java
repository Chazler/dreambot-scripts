package com.allinone.skills.combat.nodes;

import com.allinone.skills.combat.data.LocationDef;
import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.utilities.Sleep;

public class TravelNode extends LeafNode {
    private final Blackboard blackboard;

    public TravelNode(Blackboard blackboard) {
        super();
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        LocationDef targetLoc = blackboard.getBestLocation();
        if (targetLoc == null) {
            log("No target location set!");
            return Status.FAILURE;
        }
        
        if (targetLoc.getArea().contains(Players.getLocal())) {
            return Status.SUCCESS;
        }

        // If we are performing an animation (like climbing stairs), wait
        boolean isAnimating = Players.getLocal().isAnimating();
        if (isAnimating) {
            log("[Travel] Animating (Stairs/Interact). Waiting...");
            return Status.RUNNING;
        }

        blackboard.setCurrentStatus("Traveling to " + targetLoc.getName());
        com.allinone.framework.TravelHelper.travelTo(targetLoc.getArea());
        
        return Status.RUNNING;
    }
}
