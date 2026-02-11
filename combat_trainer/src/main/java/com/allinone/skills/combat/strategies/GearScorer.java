package com.allinone.skills.combat.strategies;

import com.allinone.skills.combat.data.GearItem;
import com.allinone.skills.combat.data.StaticGearData;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GearScorer {

    public static List<GearItem> getBestLoadout(Skills skills) {
        int att = Skills.getRealLevel(Skill.ATTACK);
        int def = Skills.getRealLevel(Skill.DEFENCE);
        
        // This is a simplified "best" algorithm. 
        // In a real scenario, you'd separate by slot and maximize bonuses.
        
        return StaticGearData.ALL_GEAR.stream()
                .filter(g -> g.getAttackLevelReq() <= att)
                .filter(g -> g.getDefenceLevelReq() <= def)
                .sorted(Comparator.comparingInt(GearItem::getStrengthBonus).reversed()) // Prioritize Strength
                .collect(Collectors.toList());
    }
}
