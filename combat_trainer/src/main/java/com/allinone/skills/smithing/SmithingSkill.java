package com.allinone.skills.smithing;

import com.allinone.framework.AbstractSkillSet;
import com.allinone.framework.Blackboard;
import com.allinone.framework.Node;
import com.allinone.framework.Selector;
import com.allinone.framework.Sequence;
import com.allinone.skills.smithing.nodes.BankSmithingNode;
import com.allinone.skills.smithing.nodes.SmeltNode;
import com.allinone.skills.smithing.nodes.SmithItemNode;
import com.allinone.skills.smithing.nodes.TravelToSmithingLocationNode;
import com.allinone.framework.nodes.DismissDialogNode;
import com.allinone.ui.painters.SmithingPainter;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;

import com.allinone.skills.smithing.strategies.SmithingManager;

public class SmithingSkill extends AbstractSkillSet {

    @Override
    public int getCurrentLevel() {
        return Skills.getRealLevel(Skill.SMITHING);
    }

    @Override
    public String getName() {
        return "Smithing";
    }

    @Override
    public void onStart(Blackboard blackboard) {
         startTime = System.currentTimeMillis();
         Logger.log("Initializing Smithing Tree...");
         
         SmithingManager.clearBlacklist();

         painter = new SmithingPainter(blackboard, startTime);

         Node bank = new BankSmithingNode(blackboard);
         Node travel = new TravelToSmithingLocationNode(blackboard);

         Node action = new Selector(
             new SmeltNode(blackboard),
             new SmithItemNode(blackboard)
         );

         Node smithSequence = new Sequence(bank, travel, action);

         rootNode = new Selector(
             new DismissDialogNode(), // Priority: Handle level-up and other dialogs
             smithSequence
         );
    }
}
