package com.allinone.skills.firemaking.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.skills.firemaking.data.FiremakingLog;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.utilities.Sleep;

public class WithdrawLogsNode extends LeafNode {

    private final Blackboard blackboard;

    public WithdrawLogsNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        // Robust check: Check for any items containing "logs" (case insensitive)
        boolean hasLogs = Inventory.contains(i -> i != null && i.getName() != null && (i.getName().toLowerCase().contains("logs") || i.getName().equalsIgnoreCase("Logs")));
        boolean hasTinderbox = Inventory.contains("Tinderbox");

        if (hasLogs && hasTinderbox) {
             // We have logs and a tinderbox, proceed to burn
             // If bank is open, close it to update state or just proceed
             if (Bank.isOpen()) Bank.close();
             return Status.FAILURE; 
        }

        blackboard.setCurrentStatus("Withdrawing Logs");

        if (!Bank.isOpen()) {
            if (Bank.open()) {
                Sleep.sleepUntil(Bank::isOpen, 5000);
            }
            return Status.RUNNING;
        }
        
        // Smart Deposit: Only deposit junk (non-logs, non-tinderbox)
        // Check filtering specifically for non-null items
        boolean hasJunk = Inventory.all().stream().anyMatch(i -> 
            i != null && 
            i.getName() != null &&
            !i.getName().equals("Tinderbox") && 
            !i.getName().toLowerCase().contains("logs")
        );
        
        if (hasJunk) {
            log("Junked detected, depositing.");
            Bank.depositAllExcept(i -> i != null && i.getName() != null && (i.getName().equals("Tinderbox") || i.getName().toLowerCase().contains("logs")));
            Sleep.sleepUntil(() -> {
                return !Inventory.contains(i -> i != null && i.getName() != null && !i.getName().equals("Tinderbox") && !i.getName().toLowerCase().contains("logs"));
            }, 2000);
            return Status.RUNNING;
        }

        // Check for logs availability FIRST before grabbing tinderbox
        FiremakingLog bestLog = getBestLogAvailable();
        if (bestLog == null) {
            log("No usable logs found in bank (min 100 required)!");
            blackboard.setForceStopSkill(true);
            return Status.RUNNING; // Return RUNNING to block the Selector from proceeding to Travel
        }
        
        blackboard.setCurrentFiremakingLog(bestLog);

        // Ensure we have a tinderbox
        if (!Inventory.contains("Tinderbox")) {
            if (Bank.contains("Tinderbox")) {
                Bank.withdraw("Tinderbox");
                Sleep.sleepUntil(() -> Inventory.contains("Tinderbox"), 3000);
            } else {
                log("No Tinderbox found in bank!");
                blackboard.setForceStopSkill(true); // Can't do anything without tinderbox
                return Status.RUNNING; 
            }
            return Status.RUNNING;
        }
        
        // We have tinderbox, now withdraw logs
        if (Bank.withdrawAll(bestLog.getItemName())) {
            log("Withdrawing logs.");
            Sleep.sleepUntil(() -> Inventory.contains(bestLog.getItemName()), 3000);
            Bank.close();
            log("Logs withdrawn.");
            return Status.SUCCESS;
        }

        return Status.RUNNING;
    }

    private FiremakingLog getBestLogAvailable() {
        int fmLevel = Skills.getRealLevel(Skill.FIREMAKING);
        boolean isMembers = Client.isMembers() || (Worlds.getCurrent() != null && Worlds.getCurrent().isMembers());
        int MIN_LOGS = 100;

        // Check from highest to lowest with quantity check
        if (fmLevel >= 75 && isMembers && Bank.count(FiremakingLog.MAGIC.getItemName()) >= MIN_LOGS) return FiremakingLog.MAGIC;
        if (fmLevel >= 60 && Bank.count(FiremakingLog.YEW.getItemName()) >= MIN_LOGS) return FiremakingLog.YEW;
        if (fmLevel >= 45 && isMembers && Bank.count(FiremakingLog.MAPLE.getItemName()) >= MIN_LOGS) return FiremakingLog.MAPLE;
        if (fmLevel >= 30 && Bank.count(FiremakingLog.WILLOW.getItemName()) >= MIN_LOGS) return FiremakingLog.WILLOW;
        if (fmLevel >= 15 && Bank.count(FiremakingLog.OAK.getItemName()) >= MIN_LOGS) return FiremakingLog.OAK;
        if (Bank.count(FiremakingLog.NORMAL.getItemName()) >= MIN_LOGS) return FiremakingLog.NORMAL;
        
        return null; // None found
    }
}
