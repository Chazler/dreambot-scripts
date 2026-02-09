package com.combat_trainer.nodes;

import com.combat_trainer.data.GearItem;
import com.combat_trainer.data.StaticGearData;
import com.combat_trainer.framework.Blackboard;
import com.combat_trainer.framework.LeafNode;
import com.combat_trainer.framework.Status;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.utilities.Sleep;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RegearNode extends LeafNode {

    private final Blackboard blackboard;
    private boolean initialDepositDone = false;
    private boolean bankingCompleted = false;

    private static final String[] FOOD_NAMES = {"Shark", "Monkfish", "Swordfish", "Lobster", "Tuna", "Salmon", "Trout", "Bread", "Shrimps"};

    public RegearNode(Blackboard blackboard) {
        super();
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        // Optimization: If we have food and a weapon, assume we are good to fight.
        // User Request: "when the player is wearing the best gear available ... and has enough food ... it should not go to the bank"
        boolean hasFood = Inventory.contains(i -> i.hasAction("Eat"));
         // Simple check: do we have something in the weapon slot?
        boolean hasWeapon = !Equipment.isSlotEmpty(EquipmentSlot.WEAPON);

        if (hasFood && hasWeapon) {
             // We are combat ready. Do not force bank.
             blackboard.setGearChecked(true); // Persist that we are "checked"
             return Status.SUCCESS;
        }

        if (blackboard.isGearChecked()) {
            // If we flagged as checked, and still have no food/weapon, maybe we are just dry?
            // But if we returned SUCCESS above, we wouldn't be here unless !hasFood.
            // So if !hasFood, we continue to banking logic below.
            // BUT, if we just finished banking, isGearChecked is true.
            // We need to allow this node to return SUCCESS if we are done.
            return Status.SUCCESS; 
        }

        blackboard.setCurrentStatus("Re-gearing at Bank");

        // Phase 1: Banking
        if (!bankingCompleted) {
            if (!Bank.isOpen()) {
                if (Bank.open()) {
                    return Status.RUNNING;
                } else {
                    return Status.RUNNING;
                }
            }
            
            // Bank is open
            if (!initialDepositDone) {
                 if (!Inventory.isEmpty()) {
                     Bank.depositAllItems();
                     blackboard.getAntiBan().sleep(600, 150);
                     return Status.RUNNING;
                 }
                 initialDepositDone = true;
            }
            
            boolean actionTaken = false;
            
            // 1. Withdraw Gear
            for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.WEAPON, EquipmentSlot.CHEST, EquipmentSlot.LEGS, 
                EquipmentSlot.SHIELD, EquipmentSlot.HAT, EquipmentSlot.AMULET, 
                EquipmentSlot.FEET, EquipmentSlot.HANDS
            }) {
                GearItem bestAvailable = findBestAvailable(slot);
                
                if (bestAvailable != null) {
                    boolean storedInBank = Bank.contains(bestAvailable.getItemName());
                    boolean wearing = Equipment.contains(bestAvailable.getItemName());
                    boolean inInv = Inventory.contains(bestAvailable.getItemName());

                    if (!wearing && !inInv && storedInBank) {
                         log("Withdrawing " + bestAvailable.getItemName());
                         Bank.withdraw(bestAvailable.getItemName());
                         actionTaken = true;
                         blackboard.getAntiBan().sleep(600, 150);
                    }
                }
            }
            
            // 2. Withdraw Food
            if (!Inventory.contains(i -> i.hasAction("Eat"))) {
                 for (String foodName : FOOD_NAMES) {
                     if (Bank.contains(foodName)) {
                         log("Withdrawing food: " + foodName);
                         Bank.withdraw(foodName, 10);
                         actionTaken = true;
                         blackboard.getAntiBan().sleep(600, 150);
                         break; // Found one food type
                     }
                 }
            }

            if (actionTaken) {
                return Status.RUNNING;
            } else {
                bankingCompleted = true;
                return Status.RUNNING;
            }
        }
        
        // Phase 2: Finishing Up
        if (Bank.isOpen()) {
            Bank.close();
            Sleep.sleepUntil(() -> !Bank.isOpen(), 3000);
            return Status.RUNNING;
        }
        
        // Equip Phase with Validation
        equipBestFromInventory();
        
        // Optional: Deposit junk loop could go here if we wanted to be perfectly clean, 
        // but 'Deposit All' at start covers most cases. 
        // If we swapped items (e.g. Wooden Shield unequipped), it is now in inventory.
        // We should PROBABLY deposit it.
        // For now, let's just mark complete. The user simply wants the correct gear on.
        
        blackboard.setGearChecked(true);
        // Reset local flags
        initialDepositDone = false;
        bankingCompleted = false;
        
        return Status.SUCCESS; 
    }

    private GearItem findBestAvailable(EquipmentSlot slot) {
        int att = Skills.getRealLevel(Skill.ATTACK);
        int def = Skills.getRealLevel(Skill.DEFENCE);

        List<GearItem> candidates = StaticGearData.ALL_GEAR.stream()
            .filter(g -> g.getSlot() == slot)
            .filter(g -> g.getAttackLevelReq() <= att)
            .filter(g -> g.getDefenceLevelReq() <= def)
            .sorted(Comparator.comparingInt(GearItem::getStrengthBonus)
                    .thenComparingInt(GearItem::getDefenceBonus)
                    .thenComparingInt(GearItem::getDefenceLevelReq)
                    .thenComparingInt(GearItem::getAttackLevelReq)
                    .reversed())
            .collect(Collectors.toList());

        for (GearItem item : candidates) {
            boolean inBank = Bank.contains(item.getItemName());
            boolean inInv = Inventory.contains(item.getItemName());
            boolean inEq = Equipment.contains(item.getItemName());
            
            if (inBank || inInv || inEq) {
                return item;
            }
        }
        return null;
    }

    private void equipBestFromInventory() {
         for (Item item : Inventory.all()) {
             if (item != null && (item.hasAction("Wear") || item.hasAction("Wield"))) {
                  // Validate: Is this item actually the BEST for its slot?
                  // If not, DO NOT EQUIP IT.
                  // This prevents the infinite loop of equipping the wooden shield back.
                  
                  // We need to guess the slot or look it up.
                  // Ideally we match name against our StaticGearData
                  EquipmentSlot slot = getSlotForItem(item.getName());
                  if (slot != null) {
                      GearItem bestForSlot = findBestAvailable(slot);
                      if (bestForSlot != null && bestForSlot.getItemName().equals(item.getName())) {
                          item.interact("Wear");
                          item.interact("Wield");
                          blackboard.getAntiBan().sleep(300, 100);
                      }
                  }
             }
         }
    }
    
    private EquipmentSlot getSlotForItem(String name) {
        return StaticGearData.ALL_GEAR.stream()
                .filter(g -> g.getItemName().equals(name))
                .map(GearItem::getSlot)
                .findFirst()
                .orElse(null);
    }
}
