package com.allinone.skills.cooking;

import com.allinone.framework.AbstractSkillSet;
import com.allinone.framework.Blackboard;
import com.allinone.framework.Node;
import com.allinone.framework.Selector;
import com.allinone.framework.Sequence;
import com.allinone.framework.nodes.DismissDialogNode;
import com.allinone.skills.cooking.nodes.BankCookingNode;
import com.allinone.skills.cooking.nodes.CookNode;
import com.allinone.skills.cooking.nodes.TravelToCookingLocationNode;
import com.allinone.skills.cooking.strategies.CookingManager;
import com.allinone.ui.painters.CookingPainter;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;

public class CookingSkill extends AbstractSkillSet {

    @Override
    public int getCurrentLevel() {
        return Skills.getRealLevel(Skill.COOKING);
    }

    @Override
    public String getName() {
        return "Cooking";
    }

    @Override
    public void onStart(Blackboard blackboard) {
        startTime = System.currentTimeMillis();
        Logger.log("Initializing Cooking Behavior Tree...");

        CookingManager.clearBlacklist();

        painter = new CookingPainter(blackboard, startTime);

        Node bank = new BankCookingNode(blackboard);
        Node travel = new TravelToCookingLocationNode(blackboard);
        Node cook = new CookNode(blackboard);

        Node cookSequence = new Sequence(bank, travel, cook);

        rootNode = new Selector(
            new DismissDialogNode(),
            cookSequence
        );

        Logger.log("Cooking Tree Initialized.");
    }
}
