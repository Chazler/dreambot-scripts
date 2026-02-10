package com.allinone.framework.loadout;

public class ChickenLoadout extends CombatLoadout {

    // Overrides for Chicken killing (e.g. maybe ranged?)
    
    public ChickenLoadout() {
        setSlot(Slot.WEAPON, 
            LoadoutItems.Ranged.MAPLE_SHORTBOW,
            LoadoutItems.Ranged.WILLOW_SHORTBOW,
            LoadoutItems.Ranged.OAK_SHORTBOW,
            LoadoutItems.Ranged.SHORTBOW
        );
        
        setSlot(Slot.AMMO, 
            LoadoutItems.Ranged.ADAMANT_ARROW,
            LoadoutItems.Ranged.MITHRIL_ARROW,
            LoadoutItems.Ranged.IRON_ARROW,
            LoadoutItems.Ranged.BRONZE_ARROW
        );
        
        removeSlot(Slot.SHIELD); // Clear the shield requirement (2h weapon)
    }
}
