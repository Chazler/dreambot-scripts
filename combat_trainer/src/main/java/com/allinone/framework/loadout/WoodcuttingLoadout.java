package com.allinone.framework.loadout;

public class WoodcuttingLoadout extends Loadout {
    public WoodcuttingLoadout() {
        setSlot(Slot.WEAPON, 
            LoadoutItems.Woodcutting.DRAGON_AXE,
            LoadoutItems.Woodcutting.RUNE_AXE,
            LoadoutItems.Woodcutting.ADAMANT_AXE,
            LoadoutItems.Woodcutting.MITHRIL_AXE,
            LoadoutItems.Woodcutting.STEEL_AXE,
            LoadoutItems.Woodcutting.IRON_AXE,
            LoadoutItems.Woodcutting.BRONZE_AXE
        );
    }
}
