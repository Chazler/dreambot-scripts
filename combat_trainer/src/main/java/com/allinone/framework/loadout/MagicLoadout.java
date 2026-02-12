package com.allinone.framework.loadout;

import com.allinone.skills.magic.data.StaticMagicGearData;

public class MagicLoadout extends Loadout {

    public MagicLoadout(String staffName, String rune1, int rune1Amt,
                        String rune2, int rune2Amt, String rune3, int rune3Amt) {

        // Equipment from static data
        setSlot(Slot.BODY, StaticMagicGearData.BODY);
        setSlot(Slot.LEGS, StaticMagicGearData.LEGS);
        setSlot(Slot.HEAD, StaticMagicGearData.HEAD);
        setSlot(Slot.NECK, StaticMagicGearData.NECK);
        setSlot(Slot.CAPE, StaticMagicGearData.CAPE);

        // Staff: runtime override if specified, otherwise static default
        if (staffName != null) {
            setSlot(Slot.WEAPON, new LoadoutItem(staffName));
        } else {
            setSlot(Slot.WEAPON, StaticMagicGearData.STAVES);
        }

        // Runes: runtime inventory
        if (rune1 != null && rune1Amt > 0) addInventoryItem(rune1, rune1Amt);
        if (rune2 != null && rune2Amt > 0) addInventoryItem(rune2, rune2Amt);
        if (rune3 != null && rune3Amt > 0) addInventoryItem(rune3, rune3Amt);
    }
}
