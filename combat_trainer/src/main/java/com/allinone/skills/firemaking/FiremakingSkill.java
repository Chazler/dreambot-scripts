package com.allinone.skills.firemaking;

import com.allinone.framework.Blackboard;
import com.allinone.framework.Node;
import com.allinone.framework.Selector;
import com.allinone.framework.Sequence;
import com.allinone.framework.SkillSet;
import com.allinone.skills.firemaking.nodes.BurnLogNode;
import com.allinone.skills.firemaking.nodes.TravelToBurnAreaNode;
import com.allinone.skills.firemaking.nodes.WithdrawLogsNode;
import com.allinone.ui.painters.FiremakingPainter;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;
import java.awt.Graphics;

public class FiremakingSkill implements SkillSet {

    private Node rootNode;
    private long startTime;
    private FiremakingPainter painter;

    @Override
    public int getCurrentLevel() {
        return Skills.getRealLevel(Skill.FIREMAKING);
    }

    @Override
    public String getName() {
        return "Firemaking";
    }

    @Override
    public void onStart(Blackboard blackboard) {
         startTime = System.currentTimeMillis();
         Logger.log("Initializing Firemaking Behavior Tree...");
         
         painter = new FiremakingPainter(blackboard, startTime);
         
         Node withdrawLogs = new WithdrawLogsNode(blackboard);
         Node travel = new TravelToBurnAreaNode(blackboard);
         Node burn = new BurnLogNode(blackboard);
         
         Node burnSequence = new Sequence(travel, burn);
         
         rootNode = new Selector(
            withdrawLogs, // Will fail if we have logs
            burnSequence  // Will run if we have logs
         );
         
         Logger.log("Firemaking Tree Initialized.");
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
