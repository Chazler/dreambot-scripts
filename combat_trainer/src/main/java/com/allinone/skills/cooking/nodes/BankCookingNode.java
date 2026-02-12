package com.allinone.skills.cooking.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.framework.TravelHelper;
import com.allinone.skills.cooking.data.CookingItem;
import com.allinone.skills.cooking.data.CookingLocation;
import com.allinone.skills.cooking.strategies.CookingManager;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Sleep;

public class BankCookingNode extends LeafNode {

    private final Blackboard blackboard;

    public BankCookingNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        CookingItem item = CookingManager.getBestItem();

        if (item == null) {
            log("No valid cooking items found. Stopping skill.");
            blackboard.setForceStopSkill(true);
            return Status.FAILURE;
        }

        // If we have raw food, we're good
        if (Inventory.contains(item.getRawName())) {
            return Status.SUCCESS;
        }

        // Travel to bank area of best location
        CookingLocation loc = CookingManager.getBestLocation();
        if (loc != null && loc.getBankArea() != null) {
            if (!loc.getBankArea().contains(Players.getLocal())) {
                if (Bank.isOpen()) {
                    Bank.close();
                    return Status.RUNNING;
                }
                blackboard.setCurrentStatus("Travelling to bank");
                TravelHelper.travelTo(loc.getBankArea());
                return Status.RUNNING;
            }
        }

        // Open bank
        if (!Bank.isOpen()) {
            blackboard.setCurrentStatus("Opening bank");
            Bank.open();
            return Status.RUNNING;
        }

        blackboard.setCurrentStatus("Banking for " + item.getRawName());

        // Deposit everything
        Bank.depositAllItems();
        Sleep.sleepUntil(() -> Inventory.isEmpty(), 2000);

        // Check if we have raw food in bank
        if (!Bank.contains(item.getRawName())) {
            log("Out of " + item.getRawName() + ". Blacklisting.");
            CookingManager.blacklist(item);
            return Status.RUNNING;
        }

        // Withdraw full inventory of raw food
        Bank.withdrawAll(item.getRawName());
        Sleep.sleepUntil(() -> Inventory.contains(item.getRawName()), 2000);

        Bank.close();
        return Status.SUCCESS;
    }
}
