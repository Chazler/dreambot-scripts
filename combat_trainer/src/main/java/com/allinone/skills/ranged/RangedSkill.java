package com.allinone.skills.ranged;

import com.allinone.framework.AbstractSkillSet;
import com.allinone.framework.Blackboard;
import com.allinone.framework.Node;
import com.allinone.framework.Selector;
import com.allinone.framework.Sequence;
import com.allinone.framework.nodes.DismissDialogNode;
import com.allinone.framework.nodes.EnsureLoadoutNode;
import com.allinone.framework.loadout.RangedLoadout;
import com.allinone.skills.combat.nodes.CheckHealthNode;
import com.allinone.skills.ranged.nodes.*;
import com.allinone.ui.painters.RangedPainter;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;

public class RangedSkill extends AbstractSkillSet {

    @Override
    public int getCurrentLevel() {
        return Skills.getRealLevel(Skill.RANGED);
    }

    @Override
    public String getName() {
        return "Ranged";
    }

    @Override
    public void onStart(Blackboard blackboard) {
        startTime = System.currentTimeMillis();
        Logger.log("Initializing Ranged Behavior Tree...");

        painter = new RangedPainter(blackboard, startTime);

        Node checkHealth = new CheckHealthNode();
        Node updateStrategy = new UpdateRangedStrategyNode(blackboard);
        Node ensureGear = new EnsureLoadoutNode(RangedLoadout::new, true);
        Node manageStyle = new ManageRangedStyleNode();
        Node travel = new TravelToRangedLocationNode(blackboard);
        Node acquireTarget = new AcquireRangedTargetNode(blackboard);
        Node attack = new RangedAttackNode(blackboard);

        Node rangedSequence = new Sequence(
            updateStrategy,
            ensureGear,
            manageStyle,
            travel,
            acquireTarget,
            attack
        );

        rootNode = new Selector(
            new DismissDialogNode(),
            checkHealth,
            rangedSequence
        );

        Logger.log("Ranged Tree Initialized.");
    }
}
