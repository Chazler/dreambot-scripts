package com.allinone.skills.woodcutting;

import com.allinone.framework.Blackboard;
import com.allinone.framework.Node;
import com.allinone.framework.Selector;
import com.allinone.framework.Sequence;
import com.allinone.framework.SkillSet;
import com.allinone.skills.woodcutting.nodes.*;
import com.allinone.ui.painters.WoodcuttingPainter;
import org.dreambot.api.utilities.Logger;
import java.awt.Graphics;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;

public class WoodcuttingSkill implements SkillSet {

    private Node rootNode;
    private long startTime;
    private WoodcuttingPainter painter;

    @Override
    public int getCurrentLevel() {
        return Skills.getRealLevel(Skill.WOODCUTTING);
    }
    
    // ... getName/onStart ...
    
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
         Node withdrawAxe = new WithdrawAxeNode(blackboard);
         
         // Banking vs Dropping Logic is handled by priorities
         // If a spot is "shouldBank", BankLogsNode runs.
         // If "shouldBank" is false, BankLogsNode fails -> Selector continues to DropLogsNode.
         Node bankLogs = new BankLogsNode(blackboard);
         Node dropLogs = new DropLogNode(); // Will run if bankLogs fails (e.g. not selected or already handled)
         
         Node travel = new TravelToTreeAreaNode(blackboard);
         
         Node chopSequence = new Sequence(
             travel,
             new FindTreeNode(blackboard),
             new ChopTreeNode(blackboard)
         );
         
         // Root Selector
         rootNode = new Selector(
             updateStrategy, // Always ensure we target correct spot
             collectNests,   // Priority: Bird nests
             withdrawAxe,    // Priority: Ensure we have an axe
             bankLogs,       // Priority: Bank if full & supported
             dropLogs,       // Priority: Drop if full & not banking
             chopSequence    // Default: Travel -> Find -> Chop
         );
         
         Logger.log("Woodcutting Tree Initialized.");
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
