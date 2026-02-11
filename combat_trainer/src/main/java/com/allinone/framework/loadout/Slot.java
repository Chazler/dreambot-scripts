package com.allinone.framework.loadout;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;

/**
 * Enum representing OSRS equipment slots.
 * Wraps or mirrors the DreamBot EquipmentSlot to be used in our Loadout system.
 * We could use DreamBot's directly, but having our own allows for abstraction if the API changes,
 * or cleaner mapping if we want to combine things. 
 * 
 * However, since DreamBot already has org.dreambot.api.methods.container.impl.equipment.EquipmentSlot,
 * we might just use that one? 
 * 
 * The prompt asked to define loadout classes and "EquipmentSlot enum". 
 * Implementing our own wrapper gives us control over the "name" or iteration order if needed.
 * But to be simple, let's create a local one that maps to the API one easily.
 */
public enum Slot {
    HEAD,
    CAPE,
    NECK,
    AMMO,
    WEAPON,
    BODY,
    SHIELD,
    LEGS,
    HANDS,
    FEET,
    RING;
    
    public org.dreambot.api.methods.container.impl.equipment.EquipmentSlot toDreamBot() {
        switch(this) {
            case HEAD: return org.dreambot.api.methods.container.impl.equipment.EquipmentSlot.HAT;
            case CAPE: return org.dreambot.api.methods.container.impl.equipment.EquipmentSlot.CAPE;
            case NECK: return org.dreambot.api.methods.container.impl.equipment.EquipmentSlot.AMULET;
            case AMMO: return org.dreambot.api.methods.container.impl.equipment.EquipmentSlot.ARROWS;
            case WEAPON: return org.dreambot.api.methods.container.impl.equipment.EquipmentSlot.WEAPON;
            case BODY: return org.dreambot.api.methods.container.impl.equipment.EquipmentSlot.CHEST;
            case SHIELD: return org.dreambot.api.methods.container.impl.equipment.EquipmentSlot.SHIELD;
            case LEGS: return org.dreambot.api.methods.container.impl.equipment.EquipmentSlot.LEGS;
            case HANDS: return org.dreambot.api.methods.container.impl.equipment.EquipmentSlot.HANDS;
            case FEET: return org.dreambot.api.methods.container.impl.equipment.EquipmentSlot.FEET;
            case RING: return org.dreambot.api.methods.container.impl.equipment.EquipmentSlot.RING;
            default: return null;
        }
    }

    public static Slot fromEquipmentSlot(EquipmentSlot es) {
        if (es == null) return null;
        switch (es) {
            case HAT: return HEAD;
            case CAPE: return CAPE;
            case AMULET: return NECK;
            case ARROWS: return AMMO;
            case WEAPON: return WEAPON;
            case CHEST: return BODY;
            case SHIELD: return SHIELD;
            case LEGS: return LEGS;
            case HANDS: return HANDS;
            case FEET: return FEET;
            case RING: return RING;
            default: return null;
        }
    }
}
