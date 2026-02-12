package com.allinone.skills.ranged.data;

import com.allinone.framework.loadout.LoadoutItem;
import org.dreambot.api.methods.skills.Skill;

public class StaticRangedGearData {

    // --- WEAPONS: Bows (best to worst) ---
    public static final LoadoutItem[] WEAPONS = {
        new LoadoutItem("Magic shortbow").req(Skill.RANGED, 50),
        new LoadoutItem("Maple shortbow").req(Skill.RANGED, 30),
        new LoadoutItem("Willow shortbow").req(Skill.RANGED, 20),
        new LoadoutItem("Oak shortbow").req(Skill.RANGED, 5),
        new LoadoutItem("Shortbow").req(Skill.RANGED, 1)
    };

    // --- AMMO: Arrows (best to worst) ---
    public static final LoadoutItem[] AMMO = {
        new LoadoutItem("Adamant arrow").req(Skill.RANGED, 30),
        new LoadoutItem("Mithril arrow").req(Skill.RANGED, 20),
        new LoadoutItem("Steel arrow").req(Skill.RANGED, 5),
        new LoadoutItem("Iron arrow").req(Skill.RANGED, 1),
        new LoadoutItem("Bronze arrow").req(Skill.RANGED, 1),
    };

    // --- BODY ---
    public static final LoadoutItem[] BODY = {
        new LoadoutItem("Green d'hide body").req(Skill.RANGED, 40).req(Skill.DEFENCE, 40),
        new LoadoutItem("Studded body").req(Skill.RANGED, 20).req(Skill.DEFENCE, 20),
        new LoadoutItem("Leather body").req(Skill.RANGED, 1),
    };

    // --- LEGS ---
    public static final LoadoutItem[] LEGS = {
        new LoadoutItem("Green d'hide chaps").req(Skill.RANGED, 40),
        new LoadoutItem("Studded chaps").req(Skill.RANGED, 20),
        new LoadoutItem("Leather chaps").req(Skill.RANGED, 1),
    };

    // --- HEAD ---
    public static final LoadoutItem[] HEAD = {
        new LoadoutItem("Coif").req(Skill.RANGED, 20),
        new LoadoutItem("Leather cowl").req(Skill.RANGED, 1),
    };

    // --- HANDS ---
    public static final LoadoutItem[] HANDS = {
        new LoadoutItem("Green d'hide vambraces").req(Skill.RANGED, 40),
        new LoadoutItem("Leather vambraces").req(Skill.RANGED, 1),
    };

    // --- FEET ---
    public static final LoadoutItem[] FEET = {
        new LoadoutItem("Leather boots").req(Skill.RANGED, 1),
    };

    // --- FOOD (for safety during training) ---
    public static final String[] FOOD = {
        "Shark", "Monkfish", "Swordfish", "Lobster", "Tuna",
        "Salmon", "Trout", "Bread", "Shrimps"
    };
}
