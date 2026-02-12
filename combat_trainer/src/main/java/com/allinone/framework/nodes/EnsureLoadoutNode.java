package com.allinone.framework.nodes;

import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.framework.TravelHelper;
import com.allinone.framework.loadout.Loadout;
import com.allinone.framework.loadout.LoadoutManager;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Sleep;

import java.util.function.Supplier;

/**
 * Generic node that ensures a Loadout is satisfied.
 * Replaces WithdrawAxeNode, WithdrawPickaxeNode, WithdrawFishingGearNode.
 *
 * @param successWhenDone If false (default), returns FAILURE when satisfied (for Selector usage:
 *                        "I don't need to run, try the next child"). If true, returns SUCCESS
 *                        when satisfied (for Sequence usage: "I'm done, continue the sequence").
 */
public class EnsureLoadoutNode extends LeafNode {

    private final Supplier<Loadout> loadoutSupplier;
    private final boolean successWhenDone;

    public EnsureLoadoutNode(Supplier<Loadout> loadoutSupplier) {
        this(loadoutSupplier, false);
    }

    public EnsureLoadoutNode(Supplier<Loadout> loadoutSupplier, boolean successWhenDone) {
        this.loadoutSupplier = loadoutSupplier;
        this.successWhenDone = successWhenDone;
    }

    @Override
    public Status execute() {
        Loadout loadout = loadoutSupplier.get();

        // 1. Already satisfied? Close bank if open and signal done.
        if (LoadoutManager.isSatisfied(loadout)) {
            if (Bank.isOpen()) {
                Bank.close();
                Sleep.sleepUntil(() -> !Bank.isOpen(), 3000);
            }
            return successWhenDone ? Status.SUCCESS : Status.FAILURE;
        }

        log("Getting " + loadout.getName());

        // 2. Travel to bank if too far
        BankLocation nearest = BankLocation.getNearest(Players.getLocal());
        if (nearest != null && nearest.distance(Players.getLocal().getTile()) > 15) {
            TravelHelper.travelTo(nearest.getArea(10));
            return Status.RUNNING;
        }

        // 3. Fulfill the loadout (banking + equipping)
        boolean ready = LoadoutManager.fulfill(loadout);

        // Safety guard: only trust 'ready' if isSatisfied also agrees.
        // This prevents false positives when resolve() returns empty targets.
        if (ready && LoadoutManager.isSatisfied(loadout)) {
            if (Bank.isOpen()) {
                Bank.close();
                Sleep.sleepUntil(() -> !Bank.isOpen(), 3000);
            }
            return successWhenDone ? Status.SUCCESS : Status.FAILURE;
        }

        return Status.RUNNING;
    }
}
