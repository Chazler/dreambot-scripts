package com.allinone.skills.crafting.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.skills.crafting.data.CraftingItem;
import com.allinone.skills.crafting.data.CraftingType;
import com.allinone.skills.crafting.strategies.CraftingManager;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.utilities.Sleep;

public class BankCraftingNode extends LeafNode {

    private final Blackboard blackboard;

    public BankCraftingNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        CraftingItem item = CraftingManager.getBestItem();

        if (item == null) {
            log("No valid crafting items found. Stopping skill.");
            blackboard.setForceStopSkill(true);
            return Status.FAILURE;
        }

        // Check if we have materials
        boolean hasTools = true;
        boolean hasMaterials = true;

        if (item.getType() == CraftingType.LEATHER) {
            if (!Inventory.contains("Needle")) hasTools = false;
            if (!Inventory.contains("Thread")) hasTools = false;
        } else if (item.getType() == CraftingType.GEM) {
            if (!Inventory.contains("Chisel")) hasTools = false;
        } else if (item.getType() == CraftingType.JEWELRY) {
            // Need ring mould or necklace mould or bracelet mould
            boolean hasMould = Inventory.contains("Ring mould")
                || Inventory.contains("Necklace mould")
                || Inventory.contains("Bracelet mould")
                || Inventory.contains("Amulet mould");
            if (!hasMould) hasTools = false;
        }

        if (!Inventory.contains(item.getPrimaryItem()) || Inventory.count(item.getPrimaryItem()) < item.getPrimaryCount()) {
            hasMaterials = false;
        }
        if (item.needsSecondary() && (!Inventory.contains(item.getSecondaryItem()) || Inventory.count(item.getSecondaryItem()) < item.getSecondaryCount())) {
            hasMaterials = false;
        }

        if (hasTools && hasMaterials) {
            return Status.SUCCESS;
        }

        // Open bank
        if (!Bank.isOpen()) {
            blackboard.setCurrentStatus("Banking for crafting materials");
            Bank.open();
            return Status.RUNNING;
        }

        blackboard.setCurrentStatus("Banking for " + item.getProduceName());

        // Deposit non-tool items
        Bank.depositAllItems();
        Sleep.sleepUntil(() -> Inventory.isEmpty(), 2000);

        // Withdraw tools
        if (item.getType() == CraftingType.LEATHER) {
            withdrawIfNeeded("Needle", 1);
            withdrawIfNeeded("Thread", 5);
        } else if (item.getType() == CraftingType.GEM) {
            withdrawIfNeeded("Chisel", 1);
        } else if (item.getType() == CraftingType.JEWELRY) {
            // Determine mould type from item name
            String mouldName = getMouldName(item);
            if (mouldName != null) {
                withdrawIfNeeded(mouldName, 1);
            }
        }

        // Withdraw primary material
        if (!Bank.contains(item.getPrimaryItem())) {
            log("Out of " + item.getPrimaryItem() + ". Blacklisting.");
            CraftingManager.blacklist(item);
            return Status.RUNNING;
        }

        if (item.needsSecondary()) {
            if (!Bank.contains(item.getSecondaryItem())) {
                log("Out of " + item.getSecondaryItem() + ". Blacklisting.");
                CraftingManager.blacklist(item);
                return Status.RUNNING;
            }

            // Calculate ratio for mixed withdrawing
            int slots = 28 - Inventory.fullSlotCount();
            int perSet = item.getPrimaryCount() + item.getSecondaryCount();
            int sets = slots / perSet;
            if (sets < 1) sets = 1;

            Bank.withdraw(item.getPrimaryItem(), sets * item.getPrimaryCount());
            Sleep.sleep(600);
            Bank.withdraw(item.getSecondaryItem(), sets * item.getSecondaryCount());
            Sleep.sleep(600);
        } else {
            Bank.withdrawAll(item.getPrimaryItem());
            Sleep.sleepUntil(() -> Inventory.contains(item.getPrimaryItem()), 2000);
        }

        Bank.close();
        return Status.SUCCESS;
    }

    private void withdrawIfNeeded(String itemName, int amount) {
        if (!Inventory.contains(itemName)) {
            Bank.withdraw(itemName, amount);
            Sleep.sleepUntil(() -> Inventory.contains(itemName), 2000);
        }
    }

    private String getMouldName(CraftingItem item) {
        String name = item.getProduceName().toLowerCase();
        if (name.contains("ring")) return "Ring mould";
        if (name.contains("necklace")) return "Necklace mould";
        if (name.contains("bracelet")) return "Bracelet mould";
        if (name.contains("amulet")) return "Amulet mould";
        return "Ring mould"; // default
    }
}
