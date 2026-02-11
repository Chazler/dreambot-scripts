package com.allinone.skills.woodcutting;

import com.allinone.framework.AbstractSkillSet;
import com.allinone.framework.Blackboard;
import com.allinone.framework.Node;
import com.allinone.framework.Selector;
import com.allinone.framework.Sequence;
import com.allinone.framework.nodes.BankItemsNode;
import com.allinone.framework.nodes.DropItemsNode;
import com.allinone.framework.nodes.EnsureLoadoutNode;
import com.allinone.framework.loadout.WoodcuttingLoadout;
import com.allinone.skills.woodcutting.data.WoodcuttingSpot;
import com.allinone.skills.woodcutting.nodes.*;
import com.allinone.ui.painters.WoodcuttingPainter;
import org.dreambot.api.utilities.Logger;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;

public class WoodcuttingSkill extends AbstractSkillSet {

    @Override
    public int getCurrentLevel() {
        return Skills.getRealLevel(Skill.WOODCUTTING);
    }

    @Override
    public String getName() {
        return "Woodcutting";
    }

    @Override
    public void onStart(Blackboard blackboard) {
         startTime = System.currentTimeMillis();
         Logger.log("Initializing Woodcutting Behavior Tree...");

         painter = new WoodcuttingPainter(blackboard, startTime);

         // Nodes
         Node updateStrategy = new UpdateWoodcuttingStrategyNode(blackboard);
         Node collectNests = new CollectNestNode();

         Node ensureAxe = new EnsureLoadoutNode(WoodcuttingLoadout::new);

         Node bankLogs = new BankItemsNode(
             blackboard,
             () -> {
                 WoodcuttingSpot spot = blackboard.getCurrentWoodcuttingSpot();
                 return spot == null || spot.shouldBank();
             },
             i -> i.getName().contains("axe"),
             "Banking Logs"
         );

         Node dropLogs = new DropItemsNode(i -> i.getName().toLowerCase().contains("axe"));

         Node travel = new TravelToTreeAreaNode(blackboard);

         Node chopSequence = new Sequence(
             travel,
             new FindTreeNode(blackboard),
             new ChopTreeNode(blackboard)
         );

         rootNode = new Selector(
             ensureAxe,      // Priority: Ensure we have an axe
             updateStrategy,  // Always ensure we target correct spot
             collectNests,    // Priority: Bird nests
             bankLogs,        // Priority: Bank if full & supported
             dropLogs,        // Priority: Drop if full & not banking
             chopSequence     // Default: Travel -> Find -> Chop
         );

         Logger.log("Woodcutting Tree Initialized.");
    }
}
