package com.allinone.framework.loadout;

import com.allinone.framework.BankHelper;
import com.allinone.framework.ItemTarget;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;

import java.util.ArrayList;
import java.util.List;

public class LoadoutManager {

    /**
     * Resolves the loadout into concrete ItemTargets based on what the player actually owns.
     * MUST be called while Bank is open for accurate results if items are in the bank.
     */
    private static List<ItemTarget> resolve(Loadout loadout) {
        List<ItemTarget> targets = new ArrayList<>();

        for (Slot slot : loadout.getDefinedSlots()) {
            String choice = null;

            // 1. Check Override
            if (loadout.hasOverride(slot)) {
                choice = loadout.getOverride(slot);
            } 
            // 2. Check Candidates (Best to Worst)
            else {
                List<LoadoutItem> candidates = loadout.getCandidates(slot);
                for (LoadoutItem candidate : candidates) {
                    
                    // Check Skill Requirements first
                    if (!candidate.meetsRequirements()) {
                        continue;
                    }

                    String candidateName = candidate.getName();

                    // Check ownership (Equipped, In Bag, or In Bank)
                    if (Equipment.contains(candidateName) || 
                        Inventory.contains(candidateName) || 
                        Bank.contains(candidateName)) {
                        choice = candidateName;
                        break; // Found the best one we own
                    }
                }
            }

            if (choice != null) {
                // If it's an item for a wearable slot, we target 1 and equip it
                // Logic for distinct stackables (Arrows) would go here or be config based
                boolean isStackable = slot == Slot.AMMO;
                int amount = isStackable ? 1000 : 1;
                
                // Construct target: Name, Amount, Equip?, Fill?
                targets.add(new ItemTarget(choice, amount, true, false));
            }
        }
        
        // Add specific inventory items
        targets.addAll(loadout.getInventoryItems());

        // Add inventory candidates (first available wins)
        List<String> candidates = loadout.getInventoryCandidates();
        if (!candidates.isEmpty()) {
            for (String candidateName : candidates) {
                if (Inventory.contains(candidateName) || Bank.contains(candidateName)) {
                    int amount = loadout.getInventoryCandidateAmount();
                    boolean fill = loadout.isInventoryCandidateFill();
                    targets.add(new ItemTarget(candidateName, amount, false, fill));
                    break;
                }
            }
        }

        return targets;
    }

    public static boolean fulfill(Loadout loadout) {
        // PHASE 1: If equippable items are already in inventory, equip them FIRST.
        // This must happen before opening bank to prevent the open/close loop
        // where fulfill() opens bank and ensure() closes it to equip.
        boolean hasItemsToEquip = false;
        for (Slot slot : loadout.getDefinedSlots()) {
            if (loadout.hasOverride(slot)) {
                String name = loadout.getOverride(slot);
                if (!Equipment.contains(name) && Inventory.contains(name)) {
                    hasItemsToEquip = true;
                    break;
                }
            } else {
                for (LoadoutItem candidate : loadout.getCandidates(slot)) {
                    if (!candidate.meetsRequirements()) continue;
                    String name = candidate.getName();
                    if (Equipment.contains(name)) break; // Slot already satisfied
                    if (Inventory.contains(name)) {
                        hasItemsToEquip = true;
                        break;
                    }
                }
                if (hasItemsToEquip) break;
            }
        }

        if (hasItemsToEquip) {
            if (Bank.isOpen()) {
                Bank.close();
                Sleep.sleepUntil(() -> !Bank.isOpen(), 3000);
                return false;
            }
            for (Slot slot : loadout.getDefinedSlots()) {
                String nameToEquip = null;
                if (loadout.hasOverride(slot)) {
                    String name = loadout.getOverride(slot);
                    if (!Equipment.contains(name) && Inventory.contains(name)) {
                        nameToEquip = name;
                    }
                } else {
                    for (LoadoutItem candidate : loadout.getCandidates(slot)) {
                        if (!candidate.meetsRequirements()) continue;
                        String name = candidate.getName();
                        if (Equipment.contains(name)) break;
                        if (Inventory.contains(name)) {
                            nameToEquip = name;
                            break;
                        }
                    }
                }
                if (nameToEquip != null) {
                    final String equipName = nameToEquip;
                    Logger.log("Equipping: " + equipName);
                    if (!Inventory.interact(equipName, "Wear")) {
                        if (!Inventory.interact(equipName, "Wield")) {
                            Inventory.interact(equipName, "Equip");
                        }
                    }
                    Sleep.sleepUntil(() -> Equipment.contains(equipName), 3000);
                }
            }
            return false;
        }

        // PHASE 2: Open bank for resolve + ensure operations.
        // Only reached when nothing in inventory needs equipping.
        if (!Bank.isOpen()) {
            Bank.open();
            Sleep.sleepUntil(Bank::isOpen, 5000);
            if (!Bank.isOpen()) {
                return false;
            }
        }

        // PHASE 3: Resolve targets (bank is open) and delegate to ensure
        List<ItemTarget> targets = resolve(loadout);
        if (targets.isEmpty()) {
            return true;
        }

        return BankHelper.ensure(targets, true);
    }
    
    /**
     * Checks if the loadout is currently satisfied.
     */
    public static boolean isSatisfied(Loadout loadout) {
        if (loadout == null) return true;
        
        // We need to know what the target items ARE before we can check if we have them.
        // This is tricky without BANK access if the "best" item isn't on us.
        // However, usually we check 'isSatisfied' to avoid going to bank.
        // Weak check: Check if current equipment matches any of the candidates for each slot.
        // This assumes if we are wearing a valid candidate, we are good.
        // It does NOT enforce "Best" if we are wearing "Second Best".
        
        for (Slot slot : loadout.getDefinedSlots()) {
            // If override, we MUST have that exact item
            if (loadout.hasOverride(slot)) {
                if (!Equipment.contains(loadout.getOverride(slot))) return false;
            } else {
                // Check if we are wearing ANY valid candidate
                boolean wearingAny = false;
                for (LoadoutItem cand : loadout.getCandidates(slot)) {
                    if (Equipment.contains(cand.getName())) {
                        wearingAny = true;
                        break;
                    }
                }
                if (!wearingAny) return false;
            }
        }
        
        // Check inventory requirements
        for (ItemTarget item : loadout.getInventoryItems()) {
            if (Inventory.count(item.getName()) < item.getAmount()) {
                return false;
            }
        }

        // Check inventory candidates (need at least one of them)
        List<String> candidates = loadout.getInventoryCandidates();
        if (!candidates.isEmpty()) {
            boolean hasAny = false;
            for (String name : candidates) {
                if (Inventory.contains(name)) {
                    hasAny = true;
                    break;
                }
            }
            if (!hasAny) return false;
        }

        return true;
    }
}
