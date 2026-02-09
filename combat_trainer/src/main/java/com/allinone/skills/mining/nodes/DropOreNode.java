package com.allinone.skills.mining.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;

public class DropOreNode extends LeafNode {

    @Override
    public Status execute() {
        if (!Inventory.isFull()) return Status.FAILURE;

        // Drop ore
        // We want to keep Pickaxes!
        // Drop everything that is an "Ore" or "Uncut" (gems)?
        // Simplest: Drop all except pickaxes.
        // Inventory.dropAll(filter);
        
        Inventory.dropAll(i -> !i.getName().contains("pickaxe"));
        
        return Status.SUCCESS;
    }
}
