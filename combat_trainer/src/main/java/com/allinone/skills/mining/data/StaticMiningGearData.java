package com.allinone.skills.mining.data;

import com.allinone.framework.loadout.LoadoutItem;
import org.dreambot.api.methods.skills.Skill;

public class StaticMiningGearData {

    // --- PICKAXES: Equipped in weapon slot (best to worst) ---
    public static final LoadoutItem[] PICKAXES = {
        new LoadoutItem("Dragon pickaxe").req(Skill.MINING, 61).req(Skill.ATTACK, 60),
        new LoadoutItem("Rune pickaxe").req(Skill.MINING, 41).req(Skill.ATTACK, 40),
        new LoadoutItem("Adamant pickaxe").req(Skill.MINING, 31).req(Skill.ATTACK, 30),
        new LoadoutItem("Mithril pickaxe").req(Skill.MINING, 21).req(Skill.ATTACK, 20),
        new LoadoutItem("Black pickaxe").req(Skill.MINING, 11).req(Skill.ATTACK, 10),
        new LoadoutItem("Steel pickaxe").req(Skill.MINING, 6).req(Skill.ATTACK, 5),
        new LoadoutItem("Iron pickaxe").req(Skill.MINING, 1).req(Skill.ATTACK, 1),
        new LoadoutItem("Bronze pickaxe").req(Skill.MINING, 1).req(Skill.ATTACK, 1),
    };
}
