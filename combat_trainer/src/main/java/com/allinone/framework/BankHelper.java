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
     * Ensures items are present. Defaults to NOT cleaning other equipment/inventory.
     */
    public static boolean ensure(List<ItemTarget> targets) {
        return ensure(targets, false);
    }

    /**
     * Ensures the player has the specified items in Inventory or Equipment.
     * Called repeatedly each tick until it returns true.
     *
     * Phases (each call advances one step):
     *   1. Equip items already in inventory (closes bank if needed)
     *   2. Deposit non-target equipment (cleanEquipment only)
     *   3. Deposit non-target inventory items
     *   4. Withdraw equip items (gear before food)
     *   5. Withdraw inventory items (food, tools, etc.)
     *
     * @param targets        List of items needed.
     * @param cleanEquipment If true, deposits equipment and inventory items NOT in the target list.
     * @return true if all requirements are met, false if actions are in progress.
     */
    public static boolean ensure(List<ItemTarget> targets, boolean cleanEquipment) {
        // Separate targets into equip and inventory categories
        List<ItemTarget> equipTargets = new ArrayList<>();
        List<ItemTarget> invTargets = new ArrayList<>();
        for (ItemTarget t : targets) {
            if (t.shouldEquip()) equipTargets.add(t);
            else invTargets.add(t);
        }

        // --- PHASE 1: Equip items already in inventory ---
        boolean needEquip = false;
        for (ItemTarget t : equipTargets) {
            if (!Equipment.contains(t.getName()) && Inventory.contains(t.getName())) {
                needEquip = true;
                break;
            }
        }
        if (needEquip) {
            if (Bank.isOpen()) {
                Bank.close();
                Sleep.sleepUntil(() -> !Bank.isOpen(), 3000);
                return false;
            }
            for (ItemTarget t : equipTargets) {
                if (!Equipment.contains(t.getName()) && Inventory.contains(t.getName())) {
                    Logger.log("Equipping: " + t.getName());
                    if (!Inventory.interact(t.getName(), "Wear")) {
                        if (!Inventory.interact(t.getName(), "Wield")) {
                            Inventory.interact(t.getName(), "Equip");
                        }
                    }
                    Sleep.sleepUntil(() -> Equipment.contains(t.getName()), 3000);
                }
            }
            return false;
        }

        // --- PHASE 2: Calculate state ---
        boolean missingEquip = false;
        for (ItemTarget t : equipTargets) {
            if (!Equipment.contains(t.getName())) {
                missingEquip = true;
                break;
            }
        }

        boolean missingInv = false;
        for (ItemTarget t : invTargets) {
            boolean satisfied;
            if (t.shouldFill()) {
                satisfied = (Inventory.isFull() && Inventory.contains(t.getName()))
                         || Inventory.count(t.getName()) >= t.getAmount();
            } else {
                satisfied = Inventory.count(t.getName()) >= t.getAmount();
            }
            if (!satisfied) {
                missingInv = true;
                break;
            }
        }

        boolean hasJunkEquip = false;
        if (cleanEquipment) {
            for (org.dreambot.api.wrappers.items.Item item : Equipment.all()) {
                if (item == null) continue;
                boolean isTarget = false;
                for (ItemTarget t : equipTargets) {
                    if (t.getName().equals(item.getName())) { isTarget = true; break; }
                }
                if (!isTarget) { hasJunkEquip = true; break; }
            }
        }

        boolean hasJunkInv = false;
        if (cleanEquipment) {
            for (org.dreambot.api.wrappers.items.Item item : Inventory.all()) {
                if (item == null) continue;
                boolean isTarget = false;
                for (ItemTarget t : targets) {
                    if (t.getName().equals(item.getName())) { isTarget = true; break; }
                }
                if (!isTarget) { hasJunkInv = true; break; }
            }
        }

        // All satisfied?
        if (!missingEquip && !missingInv && !hasJunkEquip && !hasJunkInv) {
            return true;
        }

        // --- PHASE 3: Open bank ---
        if (!Bank.isOpen()) {
            openBankOrTravel();
            return false;
        }

        // --- PHASE 4: Deposit non-target equipment ---
        if (hasJunkEquip) {
            Logger.log("Dumping unnecessary equipment...");
            Bank.depositAllEquipment();
            Sleep.sleepUntil(Equipment::isEmpty, 2000);
            return false;
        }

        // --- PHASE 5: Deposit non-target inventory items ---
        if (hasJunkInv) {
            Logger.log("Depositing non-loadout inventory items...");
            for (org.dreambot.api.wrappers.items.Item item : Inventory.all()) {
                if (item == null) continue;
                boolean isTarget = false;
                for (ItemTarget t : targets) {
                    if (t.getName().equals(item.getName())) { isTarget = true; break; }
                }
                if (!isTarget) {
                    Bank.depositAll(item.getName());
                }
            }
            Sleep.sleep(600);
            return false;
        }
        // Fallback for non-clean mode: deposit all if inventory is full and we need to withdraw
        if (!cleanEquipment && (missingEquip || missingInv) && Inventory.isFull()) {
            Bank.depositAllItems();
            Sleep.sleepUntil(Inventory::isEmpty, 2000);
            return false;
        }

        // --- PHASE 6: Withdraw equip items (before food/inventory items) ---
        if (missingEquip) {
            for (ItemTarget t : equipTargets) {
                if (Equipment.contains(t.getName()) || Inventory.contains(t.getName())) continue;
                if (Bank.contains(t.getName())) {
                    Logger.log("Withdrawing: " + t.getName());
                    int needed = t.getAmount() - Inventory.count(t.getName());
                    if (needed <= 0) continue;
                    if (needed > 1) {
                        Bank.withdraw(t.getName(), needed);
                    } else {
                        Bank.withdraw(t.getName());
                    }
                    Sleep.sleepUntil(() -> Inventory.contains(t.getName()), 3000);
                } else {
                    Logger.log("Bank missing required item: " + t.getName());
                }
            }
            return false;
        }

        // --- PHASE 7: Withdraw inventory items ---
        if (missingInv) {
            for (ItemTarget t : invTargets) {
                boolean satisfied;
                if (t.shouldFill()) {
                    satisfied = (Inventory.isFull() && Inventory.contains(t.getName()))
                             || Inventory.count(t.getName()) >= t.getAmount();
                } else {
                    satisfied = Inventory.count(t.getName()) >= t.getAmount();
                }
                if (satisfied) continue;

                if (Bank.contains(t.getName())) {
                    Logger.log("Withdrawing: " + t.getName());
                    if (t.shouldFill()) {
                        Bank.withdrawAll(t.getName());
                    } else {
                        int current = Inventory.count(t.getName());
                        int needed = t.getAmount() - current;
                        if (needed <= 0) continue;
                        if (needed > 1) {
                            Bank.withdraw(t.getName(), needed);
                        } else {
                            Bank.withdraw(t.getName());
                        }
                    }
                    Sleep.sleepUntil(() -> Inventory.count(t.getName()) >= t.getAmount(), 3000);
                } else {
                    Logger.log("Bank missing required item: " + t.getName());
                }
            }
            return false;
        }

        return false;
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

        // If bank is far, stopping the script
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
            Logger.log("Approaching bank...");
            TravelHelper.travelTo(nearest.getArea(10));
        }
    }
}
