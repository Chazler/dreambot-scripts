package com.allinone.skills.smithing.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.skills.smithing.data.SmithingLocation;
import org.dreambot.api.methods.interactive.Players;
import com.allinone.framework.TravelHelper;
import com.allinone.skills.smithing.data.SmithingItem;
import com.allinone.skills.smithing.data.SmithingType;
import com.allinone.skills.smithing.strategies.SmithingManager;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.utilities.Sleep;

public class BankSmithingNode extends LeafNode {

    private final Blackboard blackboard;

    public BankSmithingNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        SmithingItem item = SmithingManager.getBestItem();
        
        if (item == null) {
            log("No valid Smithing items found (all blacklisted?). Stopping skill.");
            blackboard.setForceStopSkill(true);
            return Status.FAILURE; 
        }
        
        boolean hasTools = true;
        boolean hasIngredients = true;
        
        if (item.getType() == SmithingType.FORGING) {
             if (!Inventory.contains("Hammer")) hasTools = false;
        }
        
        if (!Inventory.contains(item.getPrimaryItem())) hasIngredients = false;
        if (item.needsSecondary() && !Inventory.contains(item.getSecondaryItem())) hasIngredients = false;
        
        // If we have everything, we are good to go
        // Note: strict count checks can be nice, but simple 'contains' works for 'do I need to bank?'
        // Better: 'contains at least required for 1 operation'
        if (Inventory.count(item.getPrimaryItem()) < item.getPrimaryCount()) hasIngredients = false;
        
        if (hasTools && hasIngredients) {
            return Status.SUCCESS;
        }
        
        // --- Travel Logic Insertion ---
        // Before banking, ensure we are at the correct bank!
        SmithingLocation bestLoc = SmithingManager.getBestLocation(item.getType());
        if (bestLoc != null && bestLoc.getBankArea() != null) {
            if (!bestLoc.getBankArea().contains(Players.getLocal())) {
                // We are not at the target bank.
                // We should travel there BEFORE withdrawing items to avoid running with weight.
                if (Bank.isOpen()) {
                    Bank.close();
                    return Status.RUNNING;
                }
                
                log("Travelling to Best Bank Area: " + bestLoc.name());
                blackboard.setCurrentStatus("Travelling to Bank (" + bestLoc.name() + ")");
                TravelHelper.travelTo(bestLoc.getBankArea());
                return Status.RUNNING;
            }
        }
        // -----------------------------
        
        // Go to Bank
        if (!Bank.isOpen()) {
            blackboard.setCurrentStatus("Opening Bank");
            Bank.open();
            return Status.RUNNING;
        }
        
        blackboard.setCurrentStatus("Banking for " + item.getProduceName());
        
        // Depositing
        Bank.depositAllItems();
        Sleep.sleepUntil(() -> Inventory.isEmpty(), 2000);
        
        // Withdrawing
        if (item.getType() == SmithingType.FORGING) {
            boolean hasHammer = Inventory.contains("Hammer") || Bank.contains("Hammer");
            
            if (!hasHammer) {
                log("No Hammer found! Switching to Smelting.");
                SmithingManager.blacklistType(SmithingType.FORGING);
                // Return RUNNING to re-evaluate next tick with new item
                return Status.RUNNING;
            }
            
            if (!Inventory.contains("Hammer")) {
                 Bank.withdraw("Hammer", 1);
                 Sleep.sleepUntil(() -> Inventory.contains("Hammer"), 2000);
            }
            
            if (Bank.contains(item.getPrimaryItem())) {
                 Bank.withdrawAll(item.getPrimaryItem());
                 Sleep.sleepUntil(() -> Inventory.isFull(), 2000);
            } else {
                 log("Out of bars: " + item.getPrimaryItem() + ". Blacklisting item.");
                 SmithingManager.blacklist(item);
                 return Status.RUNNING;
            }
        
        } else {
            // Smelting
            // Calculate Ratios
            // Simple approach: Withdraw All Primary, then Withdraw All Secondary? No, inv fills up.
            // Complex math for mixed withdrawing:
            
            // Example: Steel (1 Iron, 2 Coal). 
            // Slots available = 28.
            // 1 Set = 3 slots.
            // Sets = 28 / 3 = 9.
            // Iron = 9, Coal = 18.
            
            int slots = 28;
            int perSet = item.getPrimaryCount() + (item.needsSecondary() ? item.getSecondaryCount() : 0);
            int sets = slots / perSet;
            
            if (sets < 1) sets = 1; // Should not happen unless config error
            
            int primToWithdraw = sets * item.getPrimaryCount();
            int secToWithdraw = sets * item.getSecondaryCount();
            
            if (!Bank.contains(item.getPrimaryItem()) || Bank.count(item.getPrimaryItem()) < item.getPrimaryCount()) {
                log("Out of ore (Primary): " + item.getPrimaryItem() + ". Blacklisting item.");
                SmithingManager.blacklist(item);
                return Status.RUNNING;
            }
            
            if (item.needsSecondary()) {
                 if (!Bank.contains(item.getSecondaryItem()) || Bank.count(item.getSecondaryItem()) < item.getSecondaryCount()) {
                     log("Out of ore (Secondary): " + item.getSecondaryItem() + ". Blacklisting item.");
                     SmithingManager.blacklist(item);
                     return Status.RUNNING;
                 }
            }

            // Perform Withdraws only after checking ALL requirements (atomic check)
            Bank.withdraw(item.getPrimaryItem(), primToWithdraw);
            Sleep.sleep(600);
            
            if (item.needsSecondary()) {
                 Bank.withdraw(item.getSecondaryItem(), secToWithdraw);
                 Sleep.sleep(600);
            }
        }
        
        Bank.close();
        return Status.SUCCESS; // Or Running? Usually sequence moves to next if this succeeds?
        // Actually, if we just banked, we are ready to travel.
        // If we return SUCCESS, the Sequence moves to Travel.
    }
}
