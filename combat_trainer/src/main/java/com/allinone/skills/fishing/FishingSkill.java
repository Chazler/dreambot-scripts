package com.allinone.skills.fishing;

import com.allinone.framework.Blackboard;
import com.allinone.framework.Node;
import com.allinone.framework.Selector;
import com.allinone.framework.Sequence;
import com.allinone.framework.SkillSet;
import com.allinone.skills.fishing.nodes.*;
import com.allinone.ui.painters.FishingPainter;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;
import java.awt.Graphics;

public class FishingSkill implements SkillSet {

    private Node rootNode;
    private long startTime;
    private FishingPainter painter;

    @Override
    public int getCurrentLevel() {
        return Skills.getRealLevel(Skill.FISHING);
    }

    @Override
    public String getName() {
        return "Fishing";
    }

    @Override
    public void onStart(Blackboard blackboard) {
         startTime = System.currentTimeMillis();
         Logger.log("Initializing Fishing Behavior Tree...");
         
         painter = new FishingPainter(blackboard, startTime);
         
         Node updateStrategy = new UpdateFishingStrategyNode(blackboard);
         Node withdrawGear = new WithdrawFishingGearNode(blackboard);
         
         Node bankFish = new BankFishNode(blackboard);
         Node dropFish = new DropFishNode();
         
         Node travel = new TravelToFishNode(blackboard);
         
         Node fishSequence = new Sequence(
             travel,
             new FindFishNode(blackboard),
             new FishNode(blackboard)
         );
         
         rootNode = new Selector(
             updateStrategy,
             withdrawGear, // Priority: Ensure gear
             bankFish,
             dropFish,
             fishSequence
         );
         
         Logger.log("Fishing Tree Initialized.");
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
