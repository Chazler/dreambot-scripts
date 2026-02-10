package com.allinone.framework.loadout;

import com.allinone.framework.BankHelper;
import com.allinone.framework.ItemTarget;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;

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
                boolean isStackable = slot == Slot.AMMO || slot == Slot.WEAPON; // Simplified assumption
                int amount = isStackable ? 1000 : 1; 
                
                // Construct target: Name, Amount, Equip?, Fill?
                targets.add(new ItemTarget(choice, amount, true, false));
            }
        }
        
        // Add specific inventory items
        targets.addAll(loadout.getInventoryItems());
        
        return targets;
    }

    public static boolean fulfill(Loadout loadout) {
        // We defer to BankHelper, but first we must calculate WHAT to get.
        // Determining the 'Best Available' usually requires checking the Bank to see if we own it.
        // Ideally, we ensure bank is open first if we aren't fully equipped.
        
        // This is a simplified integration:
        // 1. We assume BankHelper can handle 'Bank.contains' checks if bank is open.
        // 2. We resolve the abstract Loadout into concrete ItemTargets.
        
        // Note: If resolve() returns items we assume exist, BankHelper.ensure() will try to get them.
        List<ItemTarget> targets = resolve(loadout);
        
        // Use the strict "Clean Equipment" = true because a Loadout fulfillment usually implies
        // we want exactly this gear and nothing else.
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
        
        return true;
    }
}
