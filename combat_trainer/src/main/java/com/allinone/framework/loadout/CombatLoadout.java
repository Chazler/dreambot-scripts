package com.allinone.framework.loadout;

public class CombatLoadout extends Loadout {
    
    // Default combat gear. Can be extended by specific mob strategies.
    
    public CombatLoadout() {
        setSlot(Slot.HEAD, "Iron full helm");
        setSlot(Slot.BODY, "Iron platebody");
        setSlot(Slot.LEGS, "Iron platelegs");
        setSlot(Slot.WEAPON, "Iron scimitar");
        setSlot(Slot.SHIELD, "Iron kiteshield");
        setSlot(Slot.FEET, "Iron boots");
        setSlot(Slot.HANDS, "Leather gloves");
        setSlot(Slot.NECK, "Amulet of power");
        setSlot(Slot.CAPE, "Team-1 cape");
    }
}
