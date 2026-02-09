package com.allinone.skills.mining;

import com.allinone.framework.Blackboard;
import com.allinone.framework.Node;
import com.allinone.framework.Selector;
import com.allinone.framework.Sequence;
import com.allinone.framework.SkillSet;
import com.allinone.skills.mining.nodes.*;
import com.allinone.ui.painters.MiningPainter;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;

import java.awt.Graphics;

public class MiningSkill implements SkillSet {

    private Node rootNode;
    private long startTime;
    private MiningPainter painter;

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
         
         // Nodes
         Node updateStrategy = new UpdateMiningStrategyNode(blackboard);
         Node withdrawPickaxe = new WithdrawPickaxeNode(blackboard);
         Node bankOre = new BankOreNode(blackboard);
         Node dropOre = new DropOreNode();
         Node travel = new TravelToMiningSpotNode(blackboard);
         
         Node mineSequence = new Sequence(
             travel,
             new FindRockNode(blackboard),
             new MineRockNode(blackboard)
         );
         
         // Root Selector
         rootNode = new Selector(
             updateStrategy, // Always ensure we target correct spot
             withdrawPickaxe, // Priority: Ensure we have a pickaxe
             bankOre,       // Priority: Bank if full & banking enabled
             dropOre,       // Priority: Drop if full & not banking
             mineSequence    // Default: Travel -> Find -> Mine
         );
         
         Logger.log("Mining Tree Initialized.");
    }

    @Override
    public Node getRootNode() {
        return rootNode;
    }

    @Override
    public void onPaint(Graphics g) {
        if (painter != null) painter.paint(g);
    }
}
