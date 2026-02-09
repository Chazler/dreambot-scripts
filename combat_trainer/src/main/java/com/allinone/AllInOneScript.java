package com.allinone;

import com.allinone.framework.Blackboard;
import com.allinone.framework.SkillSet;
import com.allinone.skills.combat.CombatSkill;
import com.allinone.skills.woodcutting.WoodcuttingSkill;
import com.allinone.skills.fishing.FishingSkill;
import com.allinone.skills.firemaking.FiremakingSkill;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Logger;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ScriptManifest(
    name = "All In One Trainer", 
    description = "Modular All-In-One Trainer using Behavior Trees", 
    author = "Copilot", 
    version = 2.2, 
    category = Category.MISC, 
    image = ""
)
public class AllInOneScript extends AbstractScript {

    private Blackboard blackboard;
    private SkillSet currentSkill;
    private List<SkillSet> availableSkills;
    
    // Timer Logic
    private long lastSwitchTime;
    private long switchInterval;
    private final Random random = new Random();
    
    // Configuration
    private static final int MIN_SWITCH_TIME_SEC = 300; // 5 minutes
    private static final int MAX_SWITCH_TIME_SEC = 1200; // 20 minutes

    @Override
    public void onStart() {
        Logger.log("Starting All In One Trainer...");
        
        // 1. Initialize Blackboard (Shared State)
        blackboard = new Blackboard();
        
        // 2. Register Skills
        availableSkills = new ArrayList<>();
        availableSkills.add(new CombatSkill());
        availableSkills.add(new WoodcuttingSkill());
        availableSkills.add(new FishingSkill());
        availableSkills.add(new FiremakingSkill());
        
        // 3. Initial Selection
        pickNextSkill(null);
        
        Logger.log("Initialization Complete.");
    }
    
    private void pickNextSkill(SkillSet excludeSkill) {
        // Calculate Weights: Inverse of Level
        // Weight = 100 / (Level + 1)
        double totalWeight = 0;
        List<Double> weights = new ArrayList<>();
        
        for (SkillSet s : availableSkills) {
            if (s == excludeSkill) {
                weights.add(0.0);
                continue;
            }
            int lvl = s.getCurrentLevel();
            double weight = 100.0 / (lvl + 1); // Higher level -> Lower weight
            // Add a small bias for very low skills? Or just this is enough.
            weights.add(weight);
            totalWeight += weight;
        }
        
        // Weighted Random Selection
        double r = random.nextDouble() * totalWeight;
        double cumulative = 0;
        SkillSet selected = null;
        
        // If totalWeight is 0 (e.g. only 1 skill and it's excluded), pick any other valid skill if possible
        if (totalWeight == 0) {
             for (SkillSet s : availableSkills) {
                 if (s != excludeSkill) {
                     selected = s; 
                     break;
                 }
             }
             if (selected == null) selected = availableSkills.get(0); // Fallback to whatever
        } else {
             selected = availableSkills.get(0); // Default
             for (int i = 0; i < availableSkills.size(); i++) {
                cumulative += weights.get(i);
                if (r <= cumulative && weights.get(i) > 0) {
                    selected = availableSkills.get(i);
                    break;
                }
             }
        }
        
        // Do nothing if same skill selected? 
        // Or re-initialize? Let's re-initialize to be safe/consistent
        currentSkill = selected;
        currentSkill.onStart(blackboard);
        
        // Reset Timer
        int seconds = MIN_SWITCH_TIME_SEC + random.nextInt(MAX_SWITCH_TIME_SEC - MIN_SWITCH_TIME_SEC);
        switchInterval = seconds * 1000L;
        lastSwitchTime = System.currentTimeMillis();
        
        Logger.log("Selected Skill: " + currentSkill.getName() + " (Level: " + currentSkill.getCurrentLevel() + ")");
        Logger.log("Training for " + (seconds / 60) + " minutes.");
    }

    @Override
    public int onLoop() {
        // Check Timer
        long elapsed = System.currentTimeMillis() - lastSwitchTime;
        // Check if time expired OR force stop requested
        if (elapsed > switchInterval || (blackboard != null && blackboard.shouldForceStopSkill())) {
            Logger.log(elapsed > switchInterval ? "Switch Timer Expired." : "Skill requested stop (e.g. out of resources).");
            boolean forceStop = blackboard != null && blackboard.shouldForceStopSkill();
            if (blackboard != null) blackboard.setForceStopSkill(false); // Reset flag
            pickNextSkill(forceStop ? currentSkill : null);
        }
        
        // Update Blackboard Timer
        if (blackboard != null) {
            blackboard.setTimeRemaining((switchInterval - elapsed));
        }
    
        if (currentSkill != null && currentSkill.getRootNode() != null) {
            // Tick the tree
            currentSkill.getRootNode().tick();
            
            // Global AntiBan check
            blackboard.getAntiBan().performTimedAntiBan();
        }
        
        // Dynamic sleep pattern
        return (int) (100 + Math.random() * 200); 
    }

    @Override
    public void onPaint(Graphics g) {
        if (currentSkill != null) {
            currentSkill.onPaint(g);
        }
        // Generic timer is now handled by SkillPainter
    }
}
