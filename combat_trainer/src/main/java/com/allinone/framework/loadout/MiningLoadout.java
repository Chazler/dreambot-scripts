package com.allinone.framework.loadout;

import com.allinone.skills.mining.data.StaticMiningGearData;

public class MiningLoadout extends Loadout {
    public MiningLoadout() {
        setSlot(Slot.WEAPON, StaticMiningGearData.PICKAXES);
    }
}
