package com.allinone.skills.fishing.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.skills.fishing.data.FishingSpot;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.utilities.Sleep;

public class BankFishNode extends LeafNode {

    private final Blackboard blackboard;

    public BankFishNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        if (!Inventory.isFull()) {
            return Status.FAILURE;
        }
        
        FishingSpot spot = blackboard.getCurrentFishingSpot();
        if (spot != null && !spot.shouldBank()) {
            return Status.FAILURE; // Leave it to dropping
        }

        blackboard.setCurrentStatus("Banking Fish");

        if (!Bank.isOpen()) {
            if (Bank.open()) {
                Sleep.sleepUntil(Bank::isOpen, 5000);
            }
            return Status.RUNNING;
        }

        if (Bank.depositAllExcept(i -> 
            i.getName().contains("net") || 
            i.getName().contains("rod") || 
            i.getName().contains("Feather") ||
            i.getName().contains("Bait") ||
            i.getName().contains("Harpoon") ||
            i.getName().contains("pot")
        )) {
             if (Inventory.isEmpty() || !Inventory.isFull()) {
                 Bank.close();
                 return Status.SUCCESS;
             }
        }
        
        return Status.RUNNING;
    }
}
