package com.allinone.framework.loadout;

import com.allinone.skills.ranged.data.StaticRangedGearData;

public class RangedLoadout extends Loadout {

    public RangedLoadout() {
        setSlot(Slot.WEAPON, StaticRangedGearData.WEAPONS);
        setSlot(Slot.AMMO,   StaticRangedGearData.AMMO);
        setSlot(Slot.BODY,   StaticRangedGearData.BODY);
        setSlot(Slot.LEGS,   StaticRangedGearData.LEGS);
        setSlot(Slot.HEAD,   StaticRangedGearData.HEAD);
        setSlot(Slot.HANDS,  StaticRangedGearData.HANDS);
        setSlot(Slot.FEET,   StaticRangedGearData.FEET);

        setInventoryCandidates(10, false, StaticRangedGearData.FOOD);
    }
}
