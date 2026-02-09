package com.allinone.skills.woodcutting.nodes;

import com.allinone.framework.BankHelper;
import com.allinone.framework.Blackboard;
import com.allinone.framework.ItemTarget;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Logger;
import java.util.Collections;

public class WithdrawAxeNode extends LeafNode {

    private final Blackboard blackboard;

    public WithdrawAxeNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        // 1. Find best axe we CAN use
        String bestAxe = getBestAxe();
        if (bestAxe == null) {
            Logger.log("No usable axe found (checked bank + inventory)!");
            // Critical failure? 
            if (Bank.isOpen()) Bank.close();
            return Status.FAILURE; 
        }

        // 2. Check if we should equip it
        boolean canEquip = canEquipAxe(bestAxe);
        
        // 3. Delegate to BankHelper
        // We only need 1 item: the best axe
        blackboard.setCurrentStatus("Preparing Axe: " + bestAxe + (canEquip ? " (Equip)" : ""));
        
        boolean ready = BankHelper.ensure(Collections.singletonList(
            new ItemTarget(bestAxe, 1, canEquip)
        ));

        if (ready) {
             return Status.FAILURE; // We have what we need, move to next node (Banking or Chopping)
        }

        return Status.RUNNING;
    }

    private boolean canEquipAxe(String axeName) {
        int attackLevel = Skills.getRealLevel(Skill.ATTACK);
        
        if (axeName.contains("Dragon")) return attackLevel >= 60;
        if (axeName.contains("Rune")) return attackLevel >= 40;
        if (axeName.contains("Adamant")) return attackLevel >= 30;
        if (axeName.contains("Mithril")) return attackLevel >= 20;
        if (axeName.contains("Black")) return attackLevel >= 10;
        if (axeName.contains("Steel")) return attackLevel >= 5;
        // Iron and Bronze
        return true; 
    }

    private String getBestAxe() {
        int wcLevel = Skills.getRealLevel(Skill.WOODCUTTING);
        
        if (wcLevel >= 61 && haveItem("Dragon axe")) return "Dragon axe";
        if (wcLevel >= 41 && haveItem("Rune axe")) return "Rune axe";
        if (wcLevel >= 31 && haveItem("Adamant axe")) return "Adamant axe";
        if (wcLevel >= 21 && haveItem("Mithril axe")) return "Mithril axe";
        if (wcLevel >= 11 && haveItem("Black axe")) return "Black axe";
        if (wcLevel >= 6 && haveItem("Steel axe")) return "Steel axe";
        if (wcLevel >= 1 && haveItem("Iron axe")) return "Iron axe";
        if (haveItem("Bronze axe")) return "Bronze axe";
        
        return null;
    }

    private boolean haveItem(String name) {
        return Bank.contains(name) || 
               org.dreambot.api.methods.container.impl.Inventory.contains(name) || 
               org.dreambot.api.methods.container.impl.equipment.Equipment.contains(name);
    }
}
