package com.allinone.skills.combat.data;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;

public class GearItem {
    private final String itemName;
    private final EquipmentSlot slot;
    private final int attackLevelReq;
    private final int defenceLevelReq;
    private final int stabBonus;
    private final int slashBonus;
    private final int crushBonus;
    private final int strengthBonus;
    private final int defenceBonus;

    public GearItem(String itemName, EquipmentSlot slot, int attackLevelReq, int defenceLevelReq, 
                    int stabBonus, int slashBonus, int crushBonus, int strengthBonus, int defenceBonus) {
        this.itemName = itemName;
        this.slot = slot;
        this.attackLevelReq = attackLevelReq;
        this.defenceLevelReq = defenceLevelReq;
        this.stabBonus = stabBonus;
        this.slashBonus = slashBonus;
        this.crushBonus = crushBonus;
        this.strengthBonus = strengthBonus;
        this.defenceBonus = defenceBonus;
    }

    public String getItemName() { return itemName; }
    public EquipmentSlot getSlot() { return slot; }
    public int getAttackLevelReq() { return attackLevelReq; }
    public int getDefenceLevelReq() { return defenceLevelReq; }
    public int getStabBonus() { return stabBonus; }
    public int getSlashBonus() { return slashBonus; }
    public int getCrushBonus() { return crushBonus; }
    public int getStrengthBonus() { return strengthBonus; }
    public int getDefenceBonus() { return defenceBonus; }
}
