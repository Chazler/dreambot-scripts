package com.allinone.framework.nodes;

import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;

import java.util.function.Predicate;

/**
 * Generic node that drops items when inventory is full.
 * Replaces DropLogNode, DropFishNode, DropOreNode.
 */
public class DropItemsNode extends LeafNode {

    private final Predicate<Item> keepFilter;

    /**
     * @param keepFilter Items matching this predicate are KEPT (not dropped).
     */
    public DropItemsNode(Predicate<Item> keepFilter) {
        this.keepFilter = keepFilter;
    }

    @Override
    public Status execute() {
        if (!Inventory.isFull()) {
            return Status.FAILURE;
        }

        Inventory.dropAll(i -> !keepFilter.test(i));
        Sleep.sleepUntil(() -> !Inventory.isFull(), 3000);
        return Status.SUCCESS;
    }
}
