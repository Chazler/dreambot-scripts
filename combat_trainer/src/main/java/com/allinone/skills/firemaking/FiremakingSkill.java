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
         Node burn = new BurnLogNode();
         
         Node burnSequence = new Sequence(travel, burn);
         
         // If we have logs, burn sequence. If no logs, withdraw.
         // But Withdraw will run if Inv empty of logs.
         // Priority:
         // 1. If we have logs -> Burn Sequence
         // 2. If no logs -> Withdraw
         
         // But "Have Logs" logic is usually handled by Leaf nodes returning Success/Failure.
         // WithdrawLogsNode returns FAILURE if we *have* logs (it shouldn't execute).
         // So:
         // Selector:
         //    WithdrawLogs (Success if it withdrew, Running if banking, Failure if we have logs or no logs in bank)
         // Wait, if WithdrawLogs returns Failure because we have logs, we proceed to next child? Yes.
         // The order in Selector is priority.
         
         // Actually, standard behavior:
         // Banking Node: Execute checks "Do I need to bank?". If yes and successful -> Success (or Running). If no (have items) -> Failure.
         // So Banking should be first.
         
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
