package com.allinone.skills.mining;

import com.allinone.framework.AbstractSkillSet;
import com.allinone.framework.Blackboard;
import com.allinone.framework.Node;
import com.allinone.framework.Selector;
import com.allinone.framework.Sequence;
import com.allinone.framework.nodes.BankItemsNode;
import com.allinone.framework.nodes.DropItemsNode;
import com.allinone.framework.nodes.EnsureLoadoutNode;
import com.allinone.framework.loadout.MiningLoadout;
import com.allinone.skills.mining.data.MiningSpot;
import com.allinone.skills.mining.nodes.*;
import com.allinone.ui.painters.MiningPainter;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;

public class MiningSkill extends AbstractSkillSet {

    @Override
    public int getCurrentLevel() {
        return Skills.getRealLevel(Skill.MINING);
    }

    @Override
    public String getName() {
        return "Mining";
    }

    @Override
    public void onStart(Blackboard blackboard) {
         startTime = System.currentTimeMillis();
         Logger.log("Initializing Mining Behavior Tree...");

         painter = new MiningPainter(blackboard, startTime);

         Node updateStrategy = new UpdateMiningStrategyNode(blackboard);

         Node ensurePickaxe = new EnsureLoadoutNode(MiningLoadout::new);

         Node bankOre = new BankItemsNode(
             blackboard,
             () -> {
                 MiningSpot spot = blackboard.getCurrentMiningSpot();
                 return spot == null || spot.shouldBank();
             },
             i -> i.getName().contains("pickaxe"),
             "Banking Ore"
         );

         Node dropOre = new DropItemsNode(i -> i.getName().contains("pickaxe"));

         Node travel = new TravelToMiningSpotNode(blackboard);

         Node mineSequence = new Sequence(
             travel,
             new FindRockNode(blackboard),
             new MineRockNode(blackboard)
         );

         rootNode = new Selector(
             updateStrategy,   // Always ensure we target correct spot
             ensurePickaxe,    // Priority: Ensure we have a pickaxe
             bankOre,          // Priority: Bank if full & banking enabled
             dropOre,          // Priority: Drop if full & not banking
             mineSequence      // Default: Travel -> Find -> Mine
         );

         Logger.log("Mining Tree Initialized.");
    }
}
