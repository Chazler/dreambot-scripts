package com.allinone.skills.magic;

import com.allinone.framework.AbstractSkillSet;
import com.allinone.framework.Blackboard;
import com.allinone.framework.Node;
import com.allinone.framework.Selector;
import com.allinone.framework.Sequence;
import com.allinone.framework.nodes.DismissDialogNode;
import com.allinone.skills.magic.nodes.*;
import com.allinone.skills.magic.strategies.MagicManager;
import com.allinone.ui.painters.MagicPainter;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;

public class MagicSkill extends AbstractSkillSet {

    @Override
    public int getCurrentLevel() {
        return Skills.getRealLevel(Skill.MAGIC);
    }

    @Override
    public String getName() {
        return "Magic";
    }

    @Override
    public void onStart(Blackboard blackboard) {
        startTime = System.currentTimeMillis();
        Logger.log("Initializing Magic Behavior Tree...");

        MagicManager.clearBlacklist();

        painter = new MagicPainter(blackboard, startTime);

        Node bank = new BankMagicNode(blackboard);
        Node travel = new TravelToMagicLocationNode(blackboard);
        Node findTarget = new FindMagicTargetNode(blackboard);
        Node castSpell = new CastSpellNode(blackboard);
        Node highAlch = new HighAlchNode(blackboard);

        // Combat magic sequence: bank -> travel -> find -> cast
        Node combatSequence = new Sequence(bank, travel, findTarget, castSpell);

        rootNode = new Selector(
            new DismissDialogNode(),
            highAlch,       // Priority: High alch if available
            combatSequence  // Fallback: Combat magic
        );

        Logger.log("Magic Tree Initialized.");
    }
}
