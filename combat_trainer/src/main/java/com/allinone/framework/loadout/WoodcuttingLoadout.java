package com.allinone.framework.loadout;

import com.allinone.skills.woodcutting.data.StaticWoodcuttingGearData;

public class WoodcuttingLoadout extends Loadout {
    public WoodcuttingLoadout() {
        setSlot(Slot.WEAPON, StaticWoodcuttingGearData.AXES);
    }
}
