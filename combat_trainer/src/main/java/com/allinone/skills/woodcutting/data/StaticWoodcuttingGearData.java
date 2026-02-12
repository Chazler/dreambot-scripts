package com.allinone.skills.woodcutting.data;

import com.allinone.framework.loadout.LoadoutItem;
import org.dreambot.api.methods.skills.Skill;

public class StaticWoodcuttingGearData {

    // --- AXES: Equipped in weapon slot (best to worst) ---
    public static final LoadoutItem[] AXES = {
        new LoadoutItem("Dragon axe").req(Skill.WOODCUTTING, 61).req(Skill.ATTACK, 60),
        new LoadoutItem("Rune axe").req(Skill.WOODCUTTING, 41).req(Skill.ATTACK, 40),
        new LoadoutItem("Adamant axe").req(Skill.WOODCUTTING, 31).req(Skill.ATTACK, 30),
        new LoadoutItem("Mithril axe").req(Skill.WOODCUTTING, 21).req(Skill.ATTACK, 20),
        new LoadoutItem("Black axe").req(Skill.WOODCUTTING, 11).req(Skill.ATTACK, 1),
        new LoadoutItem("Steel axe").req(Skill.WOODCUTTING, 6).req(Skill.ATTACK, 5),
        new LoadoutItem("Iron axe").req(Skill.WOODCUTTING, 1).req(Skill.ATTACK, 1),
        new LoadoutItem("Bronze axe").req(Skill.WOODCUTTING, 1).req(Skill.ATTACK, 1),
    };
}
