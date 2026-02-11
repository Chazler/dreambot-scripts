package com.allinone.skills.combat.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.framework.TravelHelper;
import com.allinone.framework.loadout.CombatLoadout;
import com.allinone.framework.loadout.LoadoutManager;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.Players;

/**
 * Ensures the player has the best available combat gear and food.
 * Uses the unified Loadout system via CombatLoadout + LoadoutManager.
 */
public class RegearNode extends LeafNode {

    private final Blackboard blackboard;

    public RegearNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        CombatLoadout loadout = new CombatLoadout();

        // Already satisfied? Skip.
        if (LoadoutManager.isSatisfied(loadout)) {
            return Status.SUCCESS;
        }

        // Travel to bank if too far
        BankLocation nearest = BankLocation.getNearest(Players.getLocal());
        if (nearest != null && nearest.distance(Players.getLocal().getTile()) > 15) {
            TravelHelper.travelTo(nearest.getArea(10));
            return Status.RUNNING;
        }

        blackboard.setCurrentStatus("Re-gearing");

        // Fulfill the loadout (handles deposit, withdraw, equip)
        boolean ready = LoadoutManager.fulfill(loadout);

        if (ready) {
            return Status.SUCCESS;
        }

        return Status.RUNNING;
    }
}
