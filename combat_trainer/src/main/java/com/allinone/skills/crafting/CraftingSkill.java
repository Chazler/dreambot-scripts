package com.allinone.skills.crafting;

import com.allinone.framework.AbstractSkillSet;
import com.allinone.framework.Blackboard;
import com.allinone.framework.Node;
import com.allinone.framework.Selector;
import com.allinone.framework.Sequence;
import com.allinone.framework.nodes.DismissDialogNode;
import com.allinone.skills.crafting.nodes.BankCraftingNode;
import com.allinone.skills.crafting.nodes.CraftItemNode;
import com.allinone.skills.crafting.nodes.TravelToCraftingLocationNode;
import com.allinone.skills.crafting.strategies.CraftingManager;
import com.allinone.ui.painters.CraftingPainter;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;

public class CraftingSkill extends AbstractSkillSet {

    @Override
    public int getCurrentLevel() {
        return Skills.getRealLevel(Skill.CRAFTING);
    }

    @Override
    public String getName() {
        return "Crafting";
    }

    @Override
    public void onStart(Blackboard blackboard) {
        startTime = System.currentTimeMillis();
        Logger.log("Initializing Crafting Behavior Tree...");

        CraftingManager.clearBlacklist();

        painter = new CraftingPainter(blackboard, startTime);

        Node bank = new BankCraftingNode(blackboard);
        Node travel = new TravelToCraftingLocationNode(blackboard);
        Node craft = new CraftItemNode(blackboard);

        Node craftSequence = new Sequence(bank, travel, craft);

        rootNode = new Selector(
            new DismissDialogNode(),
            craftSequence
        );

        Logger.log("Crafting Tree Initialized.");
    }
}
