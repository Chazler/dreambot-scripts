package com.allinone.skills.combat.nodes;

import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.wrappers.items.Item;

public class CheckHealthNode extends LeafNode {

    public CheckHealthNode() {
        super();
    }

    @Override
    public Status execute() {
        if (Combat.getHealthPercent() < 50) {
            log("Health low (<50%). Attempting to eat.");
            
            if (!Tabs.isOpen(Tab.INVENTORY)) {
                Tabs.open(Tab.INVENTORY);
            }
            
            Item food = Inventory.get(i -> i.hasAction("Eat"));
            if (food != null) {
                if (food.interact("Eat")) {
                    // Small sleep handled by BT execution speed usually, but here we might want to return RUNNING
                    // if we wanted to block until eaten. For now, SUCCESS means "I handled the health check".
                    return Status.SUCCESS; 
                }
            } else {
                log("No food found! FAILED to heal.");
                // This might trigger a "Bank" sequence in a more complex tree
                return Status.FAILURE;
            }
        }
        return Status.FAILURE; // We did NOT need to eat, so we "failed" to interrupt.
                               // In a Selector, FAILURE means "Continue to next node", which is what we want.
    }
}
