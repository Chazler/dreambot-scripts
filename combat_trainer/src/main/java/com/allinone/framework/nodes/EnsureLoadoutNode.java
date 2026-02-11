package com.allinone.framework.nodes;

import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.framework.TravelHelper;
import com.allinone.framework.loadout.Loadout;
import com.allinone.framework.loadout.LoadoutManager;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.Players;

import java.util.function.Supplier;

/**
 * Generic node that ensures a Loadout is satisfied.
 * Replaces WithdrawAxeNode, WithdrawPickaxeNode, WithdrawFishingGearNode.
 */
public class EnsureLoadoutNode extends LeafNode {

    private final Supplier<Loadout> loadoutSupplier;

    public EnsureLoadoutNode(Supplier<Loadout> loadoutSupplier) {
        this.loadoutSupplier = loadoutSupplier;
    }

    @Override
    public Status execute() {
        Loadout loadout = loadoutSupplier.get();

        // 1. Already satisfied? Skip.
        if (LoadoutManager.isSatisfied(loadout)) {
            return Status.FAILURE;
        }

        // 2. Travel to bank if too far
        BankLocation nearest = BankLocation.getNearest(Players.getLocal());
        if (nearest != null && nearest.distance(Players.getLocal().getTile()) > 15) {
            TravelHelper.travelTo(nearest.getArea(10));
            return Status.RUNNING;
        }

        // 3. Fulfill the loadout (banking + equipping)
        boolean ready = LoadoutManager.fulfill(loadout);

        return ready ? Status.FAILURE : Status.RUNNING;
    }
}
