package com.allinone.skills.woodcutting.nodes;

import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Map;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.methods.item.GroundItems;

public class CollectNestNode extends LeafNode {

    @Override
    public Status execute() {
        if (Inventory.isFull()) return Status.FAILURE;
        
        GroundItem nest = GroundItems.closest(i -> i != null && i.getName().toLowerCase().contains("nest"));
        
        if (nest != null && Map.canReach(nest)) {
            if (nest.interact("Take")) {
                Sleep.sleepUntil(() -> !nest.exists(), 5000);
                return Status.SUCCESS;
            }
        }
        
        return Status.FAILURE;
    }
}
