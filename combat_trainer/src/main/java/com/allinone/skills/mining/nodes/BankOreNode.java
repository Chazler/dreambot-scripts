package com.allinone.skills.mining.nodes;

import com.allinone.framework.BankHelper;
import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.skills.mining.data.MiningSpot;
import org.dreambot.api.methods.container.impl.Inventory;

import java.util.Collections;

public class BankOreNode extends LeafNode {

    private final Blackboard blackboard;

    public BankOreNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        if (!Inventory.isFull()) return Status.FAILURE; // Not full, don't bank

        MiningSpot spot = blackboard.getCurrentMiningSpot();
        if (spot != null && !spot.shouldBank()) {
            // Configured to drop, so this node passes execution to DropOreNode
            return Status.FAILURE; 
        }

        blackboard.setCurrentStatus("Banking Ore...");
        
        // Deposit all except pickaxes
        // BankHelper.depositAllExcept(itemsToKeep)
        // Or simple: deposit all, then withdraw pickaxe (handled by WithdrawPickaxeNode next loop)
        
        // For efficiency, deposit all except Pickaxe.
        // But Pickaxe names vary.
        // Let's just Deposit All. WithdrawPickaxeNode is high priority, it will re-withdraw.
        
        boolean done = BankHelper.depositAll();
        
        if (done) {
            return Status.SUCCESS;
        }
        
        return Status.RUNNING;
    }
}
