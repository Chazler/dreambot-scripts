package com.combat_trainer.nodes;

import com.combat_trainer.framework.Blackboard;
import com.combat_trainer.framework.LeafNode;
import com.combat_trainer.framework.Status;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.combat.Combat;

public class ManageAttackStyleNode extends LeafNode {

    private final Blackboard blackboard;

    public ManageAttackStyleNode(Blackboard blackboard) {
        super();
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        int att = Skills.getRealLevel(Skill.ATTACK);
        int str = Skills.getRealLevel(Skill.STRENGTH);
        int def = Skills.getRealLevel(Skill.DEFENCE);
        
        // Default Logic: Train lowest
        Skill targetSkill = Skill.STRENGTH;
        
        // Smart Logic:
        // 1. Check Location Constraints
        int defCap = 99;
        if (blackboard.getBestLocation() != null) {
            defCap = blackboard.getBestLocation().getMaxDefenceNeeded();
        }

        // Strategy:
        // 1. Train Defence IF below Cap AND (below att OR below str)
        // 2. Train Strength IF (Str < Att + 10) -- Prioritize Strength heavily for XP/hr
        // 3. Train Attack IF Str is 10 levels ahead.
        
        // Priority 1: Defence Lagging & Below Cap
        if (def < defCap && (def < att || def < str)) {
            targetSkill = Skill.DEFENCE;
        } 
        // Priority 2: Strength Focus (Keep Str ahead of Att by up to 10 levels)
        else if (str < att + 10) {
            targetSkill = Skill.STRENGTH;
        } 
        // Priority 3: Catch up Attack (Accuracy)
        else {
            targetSkill = Skill.ATTACK;
        }

        // 2. Map to Style
        CombatStyle targetStyle;
        switch (targetSkill) {
            case ATTACK:
                targetStyle = CombatStyle.ATTACK;
                break;
            case DEFENCE:
                targetStyle = CombatStyle.DEFENCE;
                break;
            case STRENGTH: 
            default:
                targetStyle = CombatStyle.STRENGTH;
                break;
        }

        // 3. Check and Switch
        if (Combat.getCombatStyle() != targetStyle) {
            String msg = "Switching style to train " + targetSkill + " (A:" + att + " S:" + str + " D:" + def + ")";
            log(msg);
            blackboard.setCurrentStatus(msg);
            
            if (Combat.setCombatStyle(targetStyle)) {
                return Status.SUCCESS;
            } else {
                return Status.RUNNING; // Trying to switch
            }
        }

        return Status.SUCCESS; // Correct style already active
    }
}
