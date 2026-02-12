package com.allinone.framework.loadout;

import com.allinone.skills.combat.data.GearItem;
import com.allinone.skills.combat.data.StaticGearData;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.skills.Skill;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Combat loadout built dynamically from StaticGearData.
 * Candidates for each slot are ordered by score (strength + defence bonus),
 * filtered by level requirements via LoadoutItem.req().
 */
public class CombatLoadout extends Loadout {

    private static final String[] FOOD_NAMES = {
        "Shark", "Monkfish", "Swordfish", "Lobster", "Tuna", "Salmon", "Trout", "Bread", "Shrimps"
    };

    private static final EquipmentSlot[] COMBAT_SLOTS = {
        EquipmentSlot.WEAPON, EquipmentSlot.CHEST, EquipmentSlot.LEGS,
        EquipmentSlot.SHIELD, EquipmentSlot.HAT, EquipmentSlot.AMULET,
        EquipmentSlot.FEET, EquipmentSlot.HANDS, EquipmentSlot.CAPE, EquipmentSlot.RING
    };

    public CombatLoadout() {
        // Build candidates for each equipment slot from StaticGearData
        for (EquipmentSlot eqSlot : COMBAT_SLOTS) {
            Slot slot = Slot.fromEquipmentSlot(eqSlot);
            if (slot == null) continue;

            // Get all gear for this slot, sorted by score (strength + defence) descending
            List<GearItem> items = StaticGearData.ALL_GEAR.stream()
                .filter(g -> g.getSlot() == eqSlot)
                .sorted(Comparator.comparingInt((GearItem g) -> g.getStrengthBonus() + g.getDefenceBonus())
                    .reversed())
                .collect(Collectors.toList());

            if (items.isEmpty()) continue;

            // Convert to LoadoutItems with skill requirements
            LoadoutItem[] candidates = items.stream()
                .map(g -> {
                    LoadoutItem li = new LoadoutItem(g.getItemName());
                    if (g.getAttackLevelReq() > 1) li.req(Skill.ATTACK, g.getAttackLevelReq());
                    if (g.getDefenceLevelReq() > 1) li.req(Skill.DEFENCE, g.getDefenceLevelReq());
                    return li;
                })
                .toArray(LoadoutItem[]::new);

            setSlot(slot, candidates);
        }

        // Food: pick the best available (first one found in bank/inventory)
        setInventoryCandidates(14, false, FOOD_NAMES);
    }
}
