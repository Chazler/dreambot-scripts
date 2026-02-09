package com.allinone.skills.woodcutting.nodes;

import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.utilities.Sleep;

public class DropLogNode extends LeafNode {

    @Override
    public Status execute() {
        if (Inventory.isFull()) {
            Inventory.dropAll(i -> i.getName().toLowerCase().contains("logs"));
            Sleep.sleepUntil(() -> !Inventory.isFull(), 3000);
            return Status.SUCCESS;
        }
        return Status.FAILURE;
    }
}
