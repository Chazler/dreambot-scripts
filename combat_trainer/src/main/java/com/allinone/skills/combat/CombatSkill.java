package com.allinone.skills.combat;

import com.allinone.framework.Blackboard;
import com.allinone.framework.Node;
import com.allinone.framework.Selector;
import com.allinone.framework.Sequence;
import com.allinone.framework.SkillSet;
import com.allinone.skills.combat.nodes.*;
import com.allinone.ui.CombatPainter;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.Client;
import java.awt.Graphics;

import org.dreambot.api.methods.skills.Skills;

public class CombatSkill implements SkillSet {

    private Node rootNode;
    private CombatPainter painter;
    private long startTime;

    @Override
    public int getCurrentLevel() {
        return Client.isLoggedIn() ? Players.getLocal().getLevel() : 3;
    }

    @Override
    public String getName() {
        return "Combat";
    }

    @Override
    public void onStart(Blackboard blackboard) {
        startTime = System.currentTimeMillis();
        Logger.log("Initializing Combat Behavior Tree...");
        
        // Initialize Painter
        painter = new CombatPainter(blackboard, startTime);

        // Define nodes
        Node checkHealth = new CheckHealthNode();
        Node regear = new RegearNode(blackboard);
        
        Node updateStrategy = new UpdateStrategyNode(blackboard);
        Node manageStyle = new ManageAttackStyleNode(blackboard);
        Node travel = new TravelNode(blackboard);
        Node acquireTarget = new AcquireTargetNode(blackboard);
        Node attack = new AttackNode(blackboard);

        // Build composites
        Node combatSequence = new Sequence( 
            updateStrategy,
            regear, // Check gear before everything else in the combat loop
            manageStyle,
            travel,
            acquireTarget,
            attack
        );

        rootNode = new Selector( 
            checkHealth, // Top priority: Interrupts
            combatSequence // Default behavior
        );
        
        Logger.log("Combat Tree initialized successfully.");
    }

    @Override
    public Node getRootNode() {
        return rootNode;
    }

    @Override
    public void onPaint(Graphics g) {
        if (painter != null) {
            painter.paint(g);
        }
    }
}
