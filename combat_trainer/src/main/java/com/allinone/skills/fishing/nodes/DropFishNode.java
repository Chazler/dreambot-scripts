package com.allinone.skills.fishing.nodes;

import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.utilities.Sleep;

public class DropFishNode extends LeafNode {

    @Override
    public Status execute() {
        if (Inventory.isFull()) {
            // Drop raw fish only. Keep tools.
            Inventory.dropAll(i -> i.getName().toLowerCase().contains("raw") || i.getName().toLowerCase().contains("shrimps")); 
            // Better filter: Don't drop specific tools
            // For simplicity: Drop anything that has "Eat" option maybe? But raw fish don't always have Eat.
            // Drop typically: "Raw *"
            
            Sleep.sleepUntil(() -> !Inventory.isFull(), 3000);
            return Status.SUCCESS;
        }
        return Status.FAILURE;
    }
}
