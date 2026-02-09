package com.allinone.skills.woodcutting.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.skills.woodcutting.data.WoodcuttingSpot;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.methods.interactive.Players;

public class BankLogsNode extends LeafNode {

    private final Blackboard blackboard;

    public BankLogsNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        if (!Inventory.isFull()) {
            return Status.FAILURE; // Not needed
        }
        
        WoodcuttingSpot spot = blackboard.getCurrentWoodcuttingSpot();
        // If spot says don't bank, we rely on DropLogNode which should have higher priority OR run this differently.
        // Actually, DropLogNode handles dropping. If we want banking, this node runs.
        // Let's assume for this "Extension" we prefer banking if 'shouldBank' is true.
        
        if (spot != null && !spot.shouldBank()) {
            return Status.FAILURE; // Let DropLogNode handle it
        }

        blackboard.setCurrentStatus("Banking Logs");

        if (!Bank.isOpen()) {
            if (Bank.open()) {
                Sleep.sleepUntil(Bank::isOpen, 5000);
            }
            return Status.RUNNING;
        }

        if (Bank.depositAllExcept(i -> i.getName().contains("axe"))) {
             if (Inventory.isEmpty() || Inventory.onlyContains(i -> i.getName().contains("axe"))) {
                 Bank.close();
                 return Status.SUCCESS;
             }
        }
        
        return Status.RUNNING;
    }
}
