package com.allinone.skills.mining.nodes;

import com.allinone.framework.BankHelper;
import com.allinone.framework.Blackboard;
import com.allinone.framework.ItemTarget;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;

public class WithdrawPickaxeNode extends LeafNode {

    private final Blackboard blackboard;

    public WithdrawPickaxeNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        // 1. Find best pickaxe we CAN use
        String bestPickaxe = getBestPickaxe();
        if (bestPickaxe == null) {
            Logger.log("No usable pickaxe found (checked bank + inventory + equipment)!");
            if (Bank.isOpen()) Bank.close();
            return Status.FAILURE; 
        }

        boolean inEquip = Equipment.contains(bestPickaxe);
        boolean inInv = Inventory.contains(bestPickaxe);
        boolean canEquip = canEquipPickaxe(bestPickaxe);

        // Satisfied if:
        // 1. Equipped (Ideal)
        // 2. In Inventory AND cannot equip (Best possible)
        if (inEquip || (inInv && !canEquip)) {
            return Status.FAILURE;
        }

        // Check if we should equip it?
        // If we are here, we either don't have it, OR we have it in inv and CAN equip it.
        // We proceed to BankHelper to handle withdrawal or equipping.

        blackboard.setCurrentStatus("Preparing Pickaxe: " + bestPickaxe);
        
        // Ensure inventory is clean before trying to get a new pickaxe logic
        // Only if we DON'T have the best pickaxe yet, and we are at the bank (or need to go)
        // Actually, if we are upgrading, we likely have old items. 
        // Let's force a deposit if we are not satisfied yet.
        // But BankHelper handles "If Inventory Full -> Deposit All". 
        // Issue: Inventory might NOT be full (just old pickaxe + 5 ores).
        
        // Explicitly handle "Upgrade Clean Up":
        // If we are about to use BankHelper, and we have items that are NOT the bestPickaxe, 
        // and we are at the bank, Deposit them.
        if (Bank.isOpen()) {
            boolean hasJunk = Inventory.all().stream().anyMatch(i -> !i.getName().equals(bestPickaxe));
            if (hasJunk) {
                 Logger.log("Depositing junk before withdrawing best pickaxe...");
                 Bank.depositAllExcept(bestPickaxe);
                 return Status.RUNNING;
            }
        }
        
        boolean ready = BankHelper.ensure(Collections.singletonList(
            new ItemTarget(bestPickaxe, 1, canEquip)
        ), true); // Clean equipment (remove heavy armor)

        if (ready) {
             return Status.FAILURE; 
        }

        return Status.RUNNING;
    }

    private String getBestPickaxe() {
        // Order from best to worst
        List<String> picks = Arrays.asList(
            "Crystal pickaxe", "Dragon pickaxe", "Rune pickaxe", 
            "Adamant pickaxe", "Mithril pickaxe", "Black pickaxe", 
            "Steel pickaxe", "Iron pickaxe", "Bronze pickaxe"
        );
        
        int miningLevel = Skills.getRealLevel(Skill.MINING);
        int attackLevel = Skills.getRealLevel(Skill.ATTACK);
        
        for (String pick : picks) {
            if (canUsePickaxe(pick, miningLevel) && (hasPickaxe(pick))) {
                return pick;
            }
        }
        return null;
    }
    
    private boolean hasPickaxe(String name) {
        return Inventory.contains(name) || Equipment.contains(name) || Bank.contains(name);
    }

    private boolean canUsePickaxe(String name, int miningLevel) {
        if (name.contains("Crystal")) return miningLevel >= 71; // And Song of the Elves... assuming user handles requirements if they have it
        if (name.contains("Dragon")) return miningLevel >= 61;
        if (name.contains("Rune")) return miningLevel >= 41;
        if (name.contains("Adamant")) return miningLevel >= 31;
        if (name.contains("Mithril")) return miningLevel >= 21;
        if (name.contains("Black")) return miningLevel >= 11;
        if (name.contains("Steel")) return miningLevel >= 6;
        if (name.contains("Iron")) return miningLevel >= 1;
        if (name.contains("Bronze")) return miningLevel >= 1;
        return false;
    }
    
    private boolean canEquipPickaxe(String name) {
        int attackLevel = Skills.getRealLevel(Skill.ATTACK);
        if (name.contains("Crystal")) return attackLevel >= 70;
        if (name.contains("Dragon")) return attackLevel >= 60;
        if (name.contains("Rune")) return attackLevel >= 40;
        if (name.contains("Adamant")) return attackLevel >= 30;
        if (name.contains("Mithril")) return attackLevel >= 20;
        if (name.contains("Black")) return attackLevel >= 10;
        if (name.contains("Steel")) return attackLevel >= 5;
        if (name.contains("Iron")) return attackLevel >= 1;
        if (name.contains("Bronze")) return attackLevel >= 1;
        return false;
    }
}
