package com.allinone.skills.combat.nodes;

import com.allinone.framework.BankHelper;
import com.allinone.framework.ItemTarget;
import com.allinone.skills.combat.data.GearItem;
import com.allinone.skills.combat.data.StaticGearData;
import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RegearNode extends LeafNode {

    private final Blackboard blackboard;
    private static final String[] FOOD_NAMES = {"Shark", "Monkfish", "Swordfish", "Lobster", "Tuna", "Salmon", "Trout", "Bread", "Shrimps"};
    
    // State machine to enforce "Deposit -> Gear -> Food" order
    private enum State {
        CHECK_STATE, DEPOSIT_ALL, PREPARE_GEAR, PREPARE_FOOD, FINISH
    }
    
    private State state = State.CHECK_STATE;
    private List<ItemTarget> gearTargets = null;
    private List<ItemTarget> foodTargets = null;

    public RegearNode(Blackboard blackboard) {
         this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        // Optimization: if fully ready, skip regear
        boolean hasFood = Inventory.contains(i -> i.hasAction("Eat"));
        boolean hasWeapon = !Equipment.isSlotEmpty(EquipmentSlot.WEAPON);

        if (hasFood && hasWeapon) {
             blackboard.setGearChecked(true); 
             state = State.CHECK_STATE;
             return Status.SUCCESS;
        }
        
        // Safety: Ensure we are at the bank before attempting complex regear logic that requires bank interaction
        org.dreambot.api.methods.container.impl.bank.BankLocation nearest = org.dreambot.api.methods.container.impl.bank.BankLocation.getNearest(org.dreambot.api.methods.interactive.Players.getLocal());
        if (nearest != null && nearest.distance(org.dreambot.api.methods.interactive.Players.getLocal()) > 15) {
            com.allinone.framework.TravelHelper.travelTo(nearest.getArea(10));
            // Reset state if we had to travel, to ensure we start clean
            state = State.CHECK_STATE; 
            return Status.RUNNING;
        }

        if (blackboard.isGearChecked()) {
            return Status.SUCCESS; 
        }

        blackboard.setCurrentStatus("Re-gearing: " + state.name());

        switch (state) {
            case CHECK_STATE:
                if (!Bank.isOpen()) {
                    Bank.open();
                    return Status.RUNNING;
                }
                
                // Calculate requirements once
                gearTargets = calculateBestGear();
                foodTargets = calculateFood();
                state = State.DEPOSIT_ALL;
                return Status.RUNNING;
                
            case DEPOSIT_ALL:
                // Ensure inventory is clean before starting
                if (BankHelper.depositAll()) {
                    state = State.PREPARE_GEAR;
                }
                return Status.RUNNING;
                
            case PREPARE_GEAR:
                // Withdraw + Equip Gear
                if (BankHelper.ensure(gearTargets)) {
                    state = State.PREPARE_FOOD;
                }
                return Status.RUNNING;
                
            case PREPARE_FOOD:
                // Withdraw Food (Inventory fill)
                if (BankHelper.ensure(foodTargets)) {
                    state = State.FINISH;
                }
                return Status.RUNNING;

            case FINISH:
                if (Bank.isOpen()) {
                    Bank.close();
                    return Status.RUNNING;
                }
                blackboard.setGearChecked(true);
                state = State.CHECK_STATE;
                return Status.SUCCESS;
        }

        return Status.FAILURE;
    }

    private List<ItemTarget> calculateBestGear() {
        List<ItemTarget> targets = new ArrayList<>();
        for (EquipmentSlot slot : new EquipmentSlot[]{
            EquipmentSlot.WEAPON, EquipmentSlot.CHEST, EquipmentSlot.LEGS, 
            EquipmentSlot.SHIELD, EquipmentSlot.HAT, EquipmentSlot.AMULET, 
            EquipmentSlot.FEET, EquipmentSlot.HANDS
        }) {
            GearItem bestAvailable = findBestAvailable(slot);
            if (bestAvailable != null) {
                // We want 1 of this item, and it SHOULD be equipped
                targets.add(new ItemTarget(bestAvailable.getItemName(), 1, true));
            }
        }
        return targets;
    }
    
    private List<ItemTarget> calculateFood() {
        List<ItemTarget> targets = new ArrayList<>();
        for (String foodName : FOOD_NAMES) {
             if (Bank.contains(foodName)) {
                 // Found best food available. Request 14.
                 // Note: ItemTarget(name, amount, equip?)
                 targets.add(new ItemTarget(foodName, 14, false)); 
                 break; 
             }
        }
        return targets;
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
}
