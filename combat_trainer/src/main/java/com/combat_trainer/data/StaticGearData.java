package com.combat_trainer.data;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import java.util.ArrayList;
import java.util.List;

public class StaticGearData {
    public static final List<GearItem> ALL_GEAR = new ArrayList<>();


    static {
        // --- WEAPONS ---
        // Bronze
        ALL_GEAR.add(new GearItem("Bronze scimitar", EquipmentSlot.WEAPON, 1, 1, 0, 7, 0, 6, 0));
        // Iron
        ALL_GEAR.add(new GearItem("Iron scimitar", EquipmentSlot.WEAPON, 1, 1, 0, 10, 0, 9, 0));
        // Steel (Att 5)
        ALL_GEAR.add(new GearItem("Steel scimitar", EquipmentSlot.WEAPON, 5, 1, 0, 15, 0, 14, 0));
        // Mithril (Att 20)
        ALL_GEAR.add(new GearItem("Mithril scimitar", EquipmentSlot.WEAPON, 20, 1, 0, 21, 0, 20, 0));
        // Adamant (Att 30)
        ALL_GEAR.add(new GearItem("Adamant scimitar", EquipmentSlot.WEAPON, 30, 1, 0, 29, 0, 28, 0));
        // Rune (Att 40)
        ALL_GEAR.add(new GearItem("Rune scimitar", EquipmentSlot.WEAPON, 40, 1, 0, 45, 0, 44, 0));
        // Dragon (Att 60)
        ALL_GEAR.add(new GearItem("Dragon scimitar", EquipmentSlot.WEAPON, 60, 1, 0, 67, 0, 66, 0));
        ALL_GEAR.add(new GearItem("Abyssal whip", EquipmentSlot.WEAPON, 70, 1, 0, 82, 0, 82, 0));

        // --- WEAPONS: SWORDS ---
        // Bronze
        ALL_GEAR.add(new GearItem("Bronze sword", EquipmentSlot.WEAPON, 1, 1, 4, 3, -2, 4, 0));
        // Iron
        ALL_GEAR.add(new GearItem("Iron sword", EquipmentSlot.WEAPON, 1, 1, 5, 4, -2, 5, 0));
        // Steel
        ALL_GEAR.add(new GearItem("Steel sword", EquipmentSlot.WEAPON, 5, 1, 8, 6, -2, 8, 0));
        // Mithril
        ALL_GEAR.add(new GearItem("Mithril sword", EquipmentSlot.WEAPON, 20, 1, 12, 10, -2, 13, 0));
        // Adamant
        ALL_GEAR.add(new GearItem("Adamant sword", EquipmentSlot.WEAPON, 30, 1, 19, 14, -2, 18, 0));
        // Rune
        ALL_GEAR.add(new GearItem("Rune sword", EquipmentSlot.WEAPON, 40, 1, 39, 29, -2, 39, 0));
        // Dragon
        ALL_GEAR.add(new GearItem("Dragon sword", EquipmentSlot.WEAPON, 60, 1, 65, 55, -2, 63, 0));

        // --- WEAPONS: LONGSWORDS ---
        // Bronze
        ALL_GEAR.add(new GearItem("Bronze longsword", EquipmentSlot.WEAPON, 1, 1, 4, 6, -2, 5, 0));
        // Iron
        ALL_GEAR.add(new GearItem("Iron longsword", EquipmentSlot.WEAPON, 1, 1, 5, 8, -2, 7, 0));
        // Steel
        ALL_GEAR.add(new GearItem("Steel longsword", EquipmentSlot.WEAPON, 5, 1, 8, 12, -2, 11, 0));
        // Mithril
        ALL_GEAR.add(new GearItem("Mithril longsword", EquipmentSlot.WEAPON, 20, 1, 12, 17, -2, 18, 0));
        // Adamant
        ALL_GEAR.add(new GearItem("Adamant longsword", EquipmentSlot.WEAPON, 30, 1, 18, 24, -2, 25, 0));
        // Rune
        ALL_GEAR.add(new GearItem("Rune longsword", EquipmentSlot.WEAPON, 40, 1, 38, 47, -2, 49, 0));
        // Dragon
        ALL_GEAR.add(new GearItem("Dragon longsword", EquipmentSlot.WEAPON, 60, 1, 58, 69, -2, 71, 0));

        // --- WEAPONS: BATTLEAXES ---
        // Bronze
        ALL_GEAR.add(new GearItem("Bronze battleaxe", EquipmentSlot.WEAPON, 1, 1, -2, 5, 4, 6, 0));
        // Iron
        ALL_GEAR.add(new GearItem("Iron battleaxe", EquipmentSlot.WEAPON, 1, 1, -2, 7, 6, 8, 0));
        // Steel
        ALL_GEAR.add(new GearItem("Steel battleaxe", EquipmentSlot.WEAPON, 5, 1, -2, 12, 11, 13, 0));
        // Mithril
        ALL_GEAR.add(new GearItem("Mithril battleaxe", EquipmentSlot.WEAPON, 20, 1, -2, 18, 16, 21, 0));
        // Adamant
        ALL_GEAR.add(new GearItem("Adamant battleaxe", EquipmentSlot.WEAPON, 30, 1, -2, 26, 23, 31, 0));
        // Rune
        ALL_GEAR.add(new GearItem("Rune battleaxe", EquipmentSlot.WEAPON, 40, 1, -2, 48, 43, 64, 0));
        // Dragon
        ALL_GEAR.add(new GearItem("Dragon battleaxe", EquipmentSlot.WEAPON, 60, 1, -2, 70, 60, 85, 0));

        // --- ARMOR: CHEST ---
        // Bronze
        ALL_GEAR.add(new GearItem("Bronze platebody", EquipmentSlot.CHEST, 1, 1, 0, 0, 0, 0, 15));
        // Iron
        ALL_GEAR.add(new GearItem("Iron platebody", EquipmentSlot.CHEST, 1, 1, 0, 0, 0, 0, 21));
        // Steel (Def 5)
        ALL_GEAR.add(new GearItem("Steel platebody", EquipmentSlot.CHEST, 1, 5, 0, 0, 0, 0, 34));
        // Mithril (Def 20)
        ALL_GEAR.add(new GearItem("Mithril platebody", EquipmentSlot.CHEST, 1, 20, 0, 0, 0, 0, 51));
        // Adamant (Def 30)
        ALL_GEAR.add(new GearItem("Adamant platebody", EquipmentSlot.CHEST, 1, 30, 0, 0, 0, 0, 66));
        // Rune (Def 40)
        ALL_GEAR.add(new GearItem("Rune platebody", EquipmentSlot.CHEST, 1, 40, 0, 0, 0, 0, 82));
        
        // --- ARMOR: LEGS ---
        // Bronze
        ALL_GEAR.add(new GearItem("Bronze platelegs", EquipmentSlot.LEGS, 1, 1, 0, 0, 0, 0, 8));
        // Iron
        ALL_GEAR.add(new GearItem("Iron platelegs", EquipmentSlot.LEGS, 1, 1, 0, 0, 0, 0, 10)); // Fixed stats
        // Steel
        ALL_GEAR.add(new GearItem("Steel platelegs", EquipmentSlot.LEGS, 1, 5, 0, 0, 0, 0, 16));
        // Mithril
        ALL_GEAR.add(new GearItem("Mithril platelegs", EquipmentSlot.LEGS, 1, 20, 0, 0, 0, 0, 24));
        // Adamant
        ALL_GEAR.add(new GearItem("Adamant platelegs", EquipmentSlot.LEGS, 1, 30, 0, 0, 0, 0, 31));
        // Rune
        ALL_GEAR.add(new GearItem("Rune platelegs", EquipmentSlot.LEGS, 1, 40, 0, 0, 0, 0, 51));

        // --- ARMOR: SHIELDS ---
        ALL_GEAR.add(new GearItem("Wooden shield", EquipmentSlot.SHIELD, 1, 1, 0, 0, 0, 0, 5));
        ALL_GEAR.add(new GearItem("Iron kiteshield", EquipmentSlot.SHIELD, 1, 1, 0, 0, 0, 0, 12));
        ALL_GEAR.add(new GearItem("Steel kiteshield", EquipmentSlot.SHIELD, 1, 5, 0, 0, 0, 0, 18));
        ALL_GEAR.add(new GearItem("Mithril kiteshield", EquipmentSlot.SHIELD, 1, 20, 0, 0, 0, 0, 26));
        ALL_GEAR.add(new GearItem("Adamant kiteshield", EquipmentSlot.SHIELD, 1, 30, 0, 0, 0, 0, 36));
        ALL_GEAR.add(new GearItem("Rune kiteshield", EquipmentSlot.SHIELD, 1, 40, 0, 0, 0, 0, 44));
        ALL_GEAR.add(new GearItem("Dragon defender", EquipmentSlot.SHIELD, 60, 60, 25, 24, 23, 6, 25));

        // --- ARMOR: HELM ---
        ALL_GEAR.add(new GearItem("Iron full helm", EquipmentSlot.HAT, 1, 1, 0, 0, 0, 0, 6));
        ALL_GEAR.add(new GearItem("Steel full helm", EquipmentSlot.HAT, 1, 5, 0, 0, 0, 0, 10));
        ALL_GEAR.add(new GearItem("Mithril full helm", EquipmentSlot.HAT, 1, 20, 0, 0, 0, 0, 12));
        ALL_GEAR.add(new GearItem("Adamant full helm", EquipmentSlot.HAT, 1, 30, 0, 0, 0, 0, 18));
        ALL_GEAR.add(new GearItem("Rune full helm", EquipmentSlot.HAT, 1, 40, 0, 0, 0, 0, 30));
        ALL_GEAR.add(new GearItem("Helm of neitiznot", EquipmentSlot.HAT, 1, 55, 0, 0, 0, 3, 30));

        // --- ACCESSORIES: NECK ---
        ALL_GEAR.add(new GearItem("Amulet of strength", EquipmentSlot.AMULET, 1, 1, 0, 0, 0, 10, 0));
        ALL_GEAR.add(new GearItem("Amulet of power", EquipmentSlot.AMULET, 1, 1, 6, 6, 6, 6, 6));
        ALL_GEAR.add(new GearItem("Amulet of glory", EquipmentSlot.AMULET, 1, 1, 10, 10, 10, 6, 3));
        ALL_GEAR.add(new GearItem("Amulet of fury", EquipmentSlot.AMULET, 1, 1, 10, 10, 10, 8, 15));
        
        // --- ACCESSORIES: FEET ---
        ALL_GEAR.add(new GearItem("Leather boots", EquipmentSlot.FEET, 1, 1, 0, 0, 0, 0, 1));
        ALL_GEAR.add(new GearItem("Climbing boots", EquipmentSlot.FEET, 1, 1, 0, 0, 0, 2, 0));
        ALL_GEAR.add(new GearItem("Rune boots", EquipmentSlot.FEET, 1, 40, 0, 0, 0, 2, 2));
        ALL_GEAR.add(new GearItem("Dragon boots", EquipmentSlot.FEET, 60, 60, 0, 0, 0, 4, 16));
        
        // --- ACCESSORIES: HANDS ---
        ALL_GEAR.add(new GearItem("Leather gloves", EquipmentSlot.HANDS, 1, 1, 0, 0, 0, 0, 2));
        ALL_GEAR.add(new GearItem("Combat bracelet", EquipmentSlot.HANDS, 1, 1, 7, 7, 7, 6, 5));
    }
}
