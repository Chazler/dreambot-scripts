package com.allinone.skills.woodcutting.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.framework.TravelHelper;
import com.allinone.framework.loadout.Loadout;
import com.allinone.framework.loadout.LoadoutManager;
import com.allinone.framework.loadout.WoodcuttingLoadout;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.Players;

public class WithdrawAxeNode extends LeafNode {

    public WithdrawAxeNode(Blackboard blackboard) {
    }

    @Override
    public Status execute() {
        Loadout wcLoadout = new WoodcuttingLoadout();

        // 1. Check if we already have what we need
        if (LoadoutManager.isSatisfied(wcLoadout)) {
            return Status.FAILURE;
        }

        // 2. Check distance to bank
        BankLocation nearest = BankLocation.getNearest(Players.getLocal());
        if (nearest != null && nearest.distance(Players.getLocal().getTile()) > 15) {
            TravelHelper.travelTo(nearest.getArea(10));
            return Status.RUNNING;
        }

        // 3. Attempt to fulfill loadout (Banking)
        boolean ready = LoadoutManager.fulfill(wcLoadout);

        if (ready) {
            return Status.FAILURE;
        }

        return Status.RUNNING;
    }
}
