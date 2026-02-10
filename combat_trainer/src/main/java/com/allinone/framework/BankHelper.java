package com.allinone.framework;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.Players;
import java.util.ArrayList;
import java.util.List;

public class BankHelper {

    /**
     * Ensures items are present. Defaults to NOT cleaning other equipment.
     */
    public static boolean ensure(List<ItemTarget> targets) {
        return ensure(targets, false);
    }

    /**
     * Ensures the player has the specified items in Inventory or Equipment.
     * Handles banking, withdrawing, and equipping.
     * 
     * @param targets List of items needed.
     * @param cleanEquipment If true, will deposit all worn equipment that is NOT in the target list.
     * @return true if all requirements are met (items present and equipped if needed), false if actions are in progress.
     */
    public static boolean ensure(List<ItemTarget> targets, boolean cleanEquipment) {
        boolean missingItems = false;
        boolean needEquip = false;

        // 0. Handle Equipment Cleaning (Dump Armor)
        if (cleanEquipment) {
            // Check if we are wearing anything NOT in targets
            boolean wearingJunk = false;
            for (org.dreambot.api.wrappers.items.Item item : Equipment.all()) {
                if (item == null) continue;
                String name = item.getName();
                boolean isTarget = targets.stream().anyMatch(t -> t.getName().equals(name) && t.shouldEquip());
                if (!isTarget) {
                    wearingJunk = true;
                    break;
                }
            }

            if (wearingJunk) {
                if (!Bank.isOpen()) {
                    openBankOrTravel();
                    return false;
                }
                Logger.log("Dumping unnecessary equipment...");
                Bank.depositAllEquipment();
                Sleep.sleepUntil(Equipment::isEmpty, 2000);
                // After dumping, we need to continue to check valid targets. 
                // We might have just dumped our Pickaxe (needed) if we defined it in targets but depositAll removed it.
                // Loop continues to withdraw section.
            }
        }

        // 1. Check State
        for (ItemTarget target : targets) {
            boolean inEquip = Equipment.contains(target.getName()); 
            
            if (target.shouldEquip()) {
                if (inEquip) continue; // Good
                // If in inventory, we need to equip
                if (Inventory.count(target.getName()) >= target.getAmount()) {
                    needEquip = true;
                    continue;
                }
                missingItems = true;
            } else if (target.shouldFill()) {
                // If filling, we are satisfied if inventory is full AND we have the item, 
                // OR if we have the specific amount requested (fallback).
                // Actually, if filling, we want as many as possible. 
                // If inventory is full, we assume we have enough? 
                // Better: If inventory is full AND contains item -> Satisfied.
                // If inventory NOT full -> Not satisfied (unless bank out of stock).
                
                // Simplified: If we have at least 'amount' (min) OR (Inv Full AND contains item).
                if ((Inventory.isFull() && Inventory.contains(target.getName())) || Inventory.count(target.getName()) >= target.getAmount()) {
                    continue;
                }
                missingItems = true;
            } else {
                // Exact calculation
                if (Inventory.count(target.getName()) >= target.getAmount()) continue;
                missingItems = true;
            }
        }

        // 2. If all good, return true
        if (!missingItems && !needEquip) {
            return true;
        }

        // 3. Handle Equipping First (if we have items in inventory but need to equip)
        // Optimization: If we have the items to equip, do it NOW before banking for food/fills
        if (needEquip && !missingItems) {
             if (Bank.isOpen()) {
                Bank.close();
                Sleep.sleepUntil(() -> !Bank.isOpen(), 3000);
                return false;
             }
             
             for (ItemTarget target : targets) {
                if (target.shouldEquip() && !Equipment.contains(target.getName()) && Inventory.contains(target.getName())) {
                    Logger.log("Equipping: " + target.getName());
                    if (!Inventory.interact(target.getName(), "Wear")) {
                        if (!Inventory.interact(target.getName(), "Wield")) {
                            Inventory.interact(target.getName(), "Equip");
                        }
                    }
                    Sleep.sleepUntil(() -> Equipment.contains(target.getName()), 3000);
                }
            }
            return false;
        }

        if (missingItems) {
            if (!Bank.isOpen()) {
                openBankOrTravel();
                return false;
            }
            
            // We are at bank.
            // If inventory is full and we have missing items, deposit all.
            // Exception: If missing item is "Fill" type, and we have *some* but inv is full, we might just be done?
            // No, logic above says we are NOT done if we land here.
            
            if (Inventory.isFull()) {
                // Check if the full inventory is actually just the stuff we want?
                // Too complex. Simple logic: Deposit all.
                Bank.depositAllItems();
                Sleep.sleepUntil(Inventory::isEmpty, 2000);
            }

            for (ItemTarget target : targets) {
                // Check satisfaction again
                if (target.shouldEquip()) {
                    // Check Equipment OR Inventory (since we just need to possess it to satisfy 'missingItems' check next loop)
                    if (Equipment.contains(target.getName()) || Inventory.contains(target.getName())) continue;
                } else {
                     if (!target.shouldFill() && Inventory.count(target.getName()) >= target.getAmount()) continue;
                }
                
                // Withdraw
                if (Bank.contains(target.getName())) {
                   Logger.log("Withdrawing: " + target.getName());
                   if (target.shouldFill()) {
                       Bank.withdrawAll(target.getName());
                   } else {
                        // Calculate how many we actually need (target - current)
                        int current = Inventory.count(target.getName());
                        int needed = target.getAmount() - current;
                        
                        if (needed <= 0) continue;

                        if (needed > 1) {
                           Bank.withdraw(target.getName(), needed);
                        } else {
                           Bank.withdraw(target.getName());
                        }
                   }
                   Sleep.sleepUntil(() -> Inventory.count(target.getName()) >= target.getAmount(), 3000);
                } else {
                    // Item missing from bank
                    Logger.log("Bank missing required item: " + target.getName());
                }
            }
            return false;
        }

        return true; // Use true if we fell through with no missing items (e.g. earlier logic passed)
    }

    public static boolean depositAll() {
        if (!Bank.isOpen()) {
            if (Bank.open()) {
                Sleep.sleepUntil(Bank::isOpen, 5000);
            } else {
                return false;
            }
        }
        
        if (Bank.isOpen()) {
             if (Inventory.isEmpty()) return true;
             Bank.depositAllItems();
             return Sleep.sleepUntil(Inventory::isEmpty, 3000);
        }
        return false;
    }

    private static void openBankOrTravel() {
        BankLocation nearest = BankLocation.getNearest(Players.getLocal());
        
        if (nearest == null) {
             Logger.log("Critical Error: No bank location found.");
             org.dreambot.api.script.ScriptManager.getScriptManager().stop();
             return;
        }

        // Safety Check: Ensure we are actually near a bank (as expected by script logic)
        // We use getArea(1) to approximate the center point
        if (nearest.getArea(1).getCenter().distance(Players.getLocal()) > 20) {
             Logger.log("Critical Error: Nearest bank is too far (>20 tiles). We should have traveled there via Nodes.");
             org.dreambot.api.script.ScriptManager.getScriptManager().stop();
             return;
        }

        if (nearest.getArea(10).contains(Players.getLocal())) {
            if (Bank.open()) {
                Sleep.sleepUntil(Bank::isOpen, 5000);
            }
        } else {
             // Close enough to interact/walk short distance
             Logger.log("Appraching bank...");
             TravelHelper.travelTo(nearest.getArea(10));
        }
    }
}
