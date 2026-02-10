package com.allinone.framework.loadout;

public class MiningLoadout extends Loadout {
    public MiningLoadout() {
        setSlot(Slot.WEAPON, 
            LoadoutItems.Mining.DRAGON_PICKAXE,
            LoadoutItems.Mining.RUNE_PICKAXE,
            LoadoutItems.Mining.ADAMANT_PICKAXE,
            LoadoutItems.Mining.MITHRIL_PICKAXE,
            LoadoutItems.Mining.STEEL_PICKAXE,
            LoadoutItems.Mining.IRON_PICKAXE,
            LoadoutItems.Mining.BRONZE_PICKAXE
        );
    }
}
