package com.allinone.skills.smithing;

import com.allinone.framework.Blackboard;
import com.allinone.framework.Node;
import com.allinone.framework.Selector;
import com.allinone.framework.Sequence;
import com.allinone.framework.SkillSet;
import com.allinone.skills.smithing.nodes.BankSmithingNode;
import com.allinone.skills.smithing.nodes.SmeltNode;
import com.allinone.skills.smithing.nodes.SmithItemNode;
import com.allinone.skills.smithing.nodes.TravelToSmithingLocationNode;
import com.allinone.ui.painters.SmithingPainter;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;
import java.awt.Graphics;

public class SmithingSkill implements SkillSet {

    private Node rootNode;
    private long startTime;
    private SmithingPainter painter;

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
         
         painter = new SmithingPainter(blackboard, startTime);
         
         Node bank = new BankSmithingNode(blackboard);
         Node travel = new TravelToSmithingLocationNode(blackboard);
         
         // Action Selector: Try Smelting, then Forging (Priority based on FAILURE return)
         Node action = new Selector(
             new SmeltNode(blackboard),
             new SmithItemNode(blackboard)
         );
         
         // Main Sequence
         // 1. Bank (Get supplies)
         // 2. Travel (Go to spot)
         // 3. Action (Do the work)
         rootNode = new Sequence(bank, travel, action);
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
