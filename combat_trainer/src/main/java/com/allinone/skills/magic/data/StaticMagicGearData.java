package com.allinone.skills.magic.data;

import com.allinone.framework.loadout.LoadoutItem;
import org.dreambot.api.methods.skills.Skill;

public class StaticMagicGearData {

    // --- STAVES: Default staff progression (best to worst) ---
    // MagicLoadout may override weapon slot at runtime based on the active spell.
    public static final LoadoutItem[] STAVES = {
        new LoadoutItem("Staff of fire").req(Skill.MAGIC, 1),
        new LoadoutItem("Staff of air").req(Skill.MAGIC, 1),
        new LoadoutItem("Staff of water").req(Skill.MAGIC, 1),
        new LoadoutItem("Staff of earth").req(Skill.MAGIC, 1),
    };

    // --- BODY: Magic robes ---
    public static final LoadoutItem[] BODY = {
        new LoadoutItem("Mystic robe top").req(Skill.MAGIC, 40).req(Skill.DEFENCE, 20),
        new LoadoutItem("Blue wizard robe"),
    };

    // --- LEGS ---
    public static final LoadoutItem[] LEGS = {
        new LoadoutItem("Mystic robe bottom").req(Skill.MAGIC, 40).req(Skill.DEFENCE, 20),
        new LoadoutItem("Blue skirt"),
    };

    // --- HEAD ---
    public static final LoadoutItem[] HEAD = {
        new LoadoutItem("Mystic hat").req(Skill.MAGIC, 40).req(Skill.DEFENCE, 20),
        new LoadoutItem("Blue wizard hat"),
    };

    // --- NECK ---
    public static final LoadoutItem[] NECK = {
        new LoadoutItem("Amulet of magic"),
    };

    // --- CAPE ---
    public static final LoadoutItem[] CAPE = {
        new LoadoutItem("God cape").req(Skill.MAGIC, 60),
    };

    // --- FOOD (for safety during combat magic) ---
    public static final String[] FOOD = {
        "Shark", "Monkfish", "Swordfish", "Lobster", "Tuna",
        "Salmon", "Trout", "Bread", "Shrimps"
    };
}
