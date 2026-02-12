package com.allinone.skills.cooking.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.framework.TravelHelper;
import com.allinone.skills.cooking.data.CookingLocation;
import com.allinone.skills.cooking.strategies.CookingManager;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.wrappers.interactive.GameObject;

public class TravelToCookingLocationNode extends LeafNode {

    private final Blackboard blackboard;

    public TravelToCookingLocationNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        CookingLocation loc = CookingManager.getBestLocation();
        if (loc == null) return Status.FAILURE;

        // Check if we're near the cooking object
        GameObject cookObj = GameObjects.closest(loc.getObjectName());
        if (cookObj != null && cookObj.distance(Players.getLocal()) < 10) {
            return Status.SUCCESS;
        }

        blackboard.setCurrentStatus("Walking to " + loc.getLocationName());
        TravelHelper.travelTo(loc.getCookingArea());
        return Status.RUNNING;
    }
}
