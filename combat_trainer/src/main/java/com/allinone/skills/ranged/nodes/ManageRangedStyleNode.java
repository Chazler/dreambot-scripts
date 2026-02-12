package com.allinone.skills.ranged.nodes;

import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;

/**
 * Ensures the attack style is set to Ranged (Accurate/Rapid/Longrange).
 * Rapid is preferred for best XP/hr.
 */
public class ManageRangedStyleNode extends LeafNode {

    @Override
    public Status execute() {
        // We want RANGED style for fastest Ranged XP
        if (Combat.getCombatStyle() != CombatStyle.RANGED) {
            log("Setting attack style to Rapid (Ranged)");
            if (Combat.setCombatStyle(CombatStyle.RANGED)) {
                return Status.SUCCESS;
            }
            return Status.RUNNING;
        }

        return Status.SUCCESS;
    }
}
