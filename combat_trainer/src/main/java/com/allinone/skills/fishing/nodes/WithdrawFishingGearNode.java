package com.allinone.skills.fishing.nodes;

import com.allinone.framework.*;
import com.allinone.skills.fishing.data.FishingMethod;
import com.allinone.skills.fishing.data.FishingSpot;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.utilities.Logger;
import java.util.ArrayList;
import java.util.List;

public class WithdrawFishingGearNode extends LeafNode {

    private final Blackboard blackboard;

    public WithdrawFishingGearNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        FishingSpot spot = blackboard.getCurrentFishingSpot();
        if (spot == null) {
            log("No fishing spot selected!");
            return Status.FAILURE;
        }

        // Determine required items
        List<ItemTarget> requiredItems = new ArrayList<>();
        
        // 1. Tool (Net, Rod, Cage, etc.)
        String tool = spot.getMethod().getToolName();
        if (tool != null && !tool.isEmpty()) {
            requiredItems.add(new ItemTarget(tool, 1, false));
        }

        // 2. Bait (if needed)
        // Check method type
        if (spot.getMethod() == FishingMethod.BAIT) {
             requiredItems.add(new ItemTarget("Fishing bait", 1, false, true)); // Fill with bait
        } else if (spot.getMethod() == FishingMethod.FLY) {
             requiredItems.add(new ItemTarget("Feather", 1, false, true)); // Fill with feathers
        }
        
        // 3. Special cases (e.g. Barbarian fishing needs heavy rod/bait? Not implemented yet)
        
        // 4. Check & Withdraw
        blackboard.setCurrentStatus("Preparing Fishing Gear");
        
        if (BankHelper.ensure(requiredItems)) {
            // If ensured, we are ready -> FAILURE so selector proceeds
            return Status.FAILURE;
        }

        return Status.RUNNING;
    }
}
