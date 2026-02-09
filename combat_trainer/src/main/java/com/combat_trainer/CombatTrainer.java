package com.combat_trainer;

import com.combat_trainer.framework.*;
import com.combat_trainer.nodes.*;
import com.combat_trainer.ui.CombatPainter;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import java.awt.*;

@ScriptManifest(
    name = "Behavior Tree Combat", 
    description = "Data-driven behavior tree combat trainer", 
    author = "Copilot", 
    version = 1.1, 
    category = Category.COMBAT, 
    image = ""
)
public class CombatTrainer extends AbstractScript {

    private Blackboard blackboard;
    private Node rootNode;
    private long startTime;
    private CombatPainter painter;

    @Override
    public void onStart() {
        startTime = System.currentTimeMillis();
        log("Initializing Behavior Tree...");
        
        // 1. Initialize Blackboard
        blackboard = new Blackboard();
        
        // 2. Initialize Painter
        painter = new CombatPainter(blackboard, startTime);

        // 3. Build Tree
        // The structure:
        // Root (Selector)
        //   ├── Interrupts (Check Health)
        //   └── Combat Loop (Sequence)
        //         ├── Update Logic (Gear/Loc)
        //         ├── Travel
        //         ├── Acquire Target
        //         └── Attack/Monitor

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
        
        log("Tree initialized successfully.");
    }

    @Override
    public int onLoop() {
        if (rootNode != null) {
            // Tick the tree
            rootNode.tick();
        }
        
        // Dynamic sleep pattern - usually BTs tick fast, but we don't want to burn CPU
        // A randomized short sleep is good for OSRS
        return (int) (100 + Math.random() * 200); 
    }

    @Override
    public void onPaint(Graphics g) {
        if (painter != null) {
            painter.paint(g);
        }
    }
}
