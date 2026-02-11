package com.allinone.skills.fishing.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.framework.loadout.FishingLoadout;
import com.allinone.framework.loadout.Loadout;
import com.allinone.framework.loadout.LoadoutManager;
import com.allinone.skills.fishing.data.FishingMethod;
import com.allinone.skills.fishing.data.FishingSpot;
import org.dreambot.api.utilities.Logger;

public class WithdrawFishingGearNode extends LeafNode {

    private final Blackboard blackboard;

    public WithdrawFishingGearNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        FishingSpot spot = blackboard.getCurrentFishingSpot();
        if (spot == null) {
            Logger.log("No fishing spot selected!");
            return Status.FAILURE;
        }

        // Determine required items
        String tool = spot.getMethod().getToolName();
        String bait = null;
        int baitAmount = 0;
        
        if (spot.getMethod() == FishingMethod.BAIT) {
             bait = "Fishing bait";
             // We generally want a good amount, but Loadout defines specific amounts.
             // Let's ask for 1000 or however many we have. 
             // FishingLoadout uses 'addInventoryItem' which implies existing stack logic if handled by user?
             // But my FishingLoadout wrapper just adds an ItemTarget.
             baitAmount = 500; 
        } else if (spot.getMethod() == FishingMethod.FLY) {
             bait = "Feather";
             baitAmount = 500;
        }

        Loadout fishingLoadout = new FishingLoadout(tool, bait, baitAmount);

        // 1. Check if we already have what we need
        if (LoadoutManager.isSatisfied(fishingLoadout)) {
            return Status.FAILURE;
        }

        // 2. Attempt to fulfill loadout (Banking)
        // LoadoutManager.fulfill now handles bank opening/travel logic via BankHelper
        boolean ready = LoadoutManager.fulfill(fishingLoadout);

        if (ready) {
            return Status.FAILURE; 
        }

        return Status.RUNNING;
    }
}
