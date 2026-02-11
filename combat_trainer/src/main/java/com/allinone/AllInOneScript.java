package com.allinone;

import com.allinone.framework.Blackboard;
import com.allinone.framework.SkillSet;
import com.allinone.skills.combat.CombatSkill;
import com.allinone.skills.woodcutting.WoodcuttingSkill;
import com.allinone.skills.fishing.FishingSkill;
import com.allinone.skills.firemaking.FiremakingSkill;
import com.allinone.skills.mining.MiningSkill;
import com.allinone.framework.nodes.custom.EdgevilleTrapdoorNode;
import com.allinone.framework.nodes.custom.EdgevilleLadderNode;
import com.allinone.framework.nodes.custom.LumbridgeSwampEntranceNode;
import com.allinone.framework.nodes.custom.LumbridgeSwampExitNode;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Logger;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.allinone.ui.SkillSelectorOverlay;
import org.dreambot.api.Client;

@ScriptManifest(
    name = "All In One Trainer", 
    description = "Modular All-In-One Trainer using Behavior Trees", 
    author = "Copilot", 
    version = 2.4, 
    category = Category.MISC, 
    image = ""
)
public class AllInOneScript extends AbstractScript {

    private Blackboard blackboard;
    private SkillSet currentSkill;
    private List<SkillSet> availableSkills;
    private SkillSelectorOverlay overlay;
    
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
        availableSkills.add(new MiningSkill());
        availableSkills.add(new com.allinone.skills.smithing.SmithingSkill());
        
        // 3. Initial Selection
        pickNextSkill(null);

        // 4. Register Custom Web Nodes (Hill Giants & Big Frogs)
        registerCustomNodes();
        
        // 5. Initialize Overlay
        overlay = new SkillSelectorOverlay(this);
        getScriptManager().addListener(overlay);
        
        Logger.log("Initialization Complete.");
    }

    public List<SkillSet> getAvailableSkills() {
        return availableSkills;
    }

    public SkillSet getCurrentSkill() {
        return currentSkill;
    }

    public void addTime(long millis) {
        switchInterval += millis;
        Logger.log("Added " + (millis / 1000 / 60) + " minutes to the timer.");
    }

    public void setCurrentSkill(SkillSet skill) {
        if (skill == null) return;
        
        currentSkill = skill;
        if (blackboard != null) blackboard.reset();
        currentSkill.onStart(blackboard);
        
        // Reset Timer
        int seconds = MIN_SWITCH_TIME_SEC + random.nextInt(MAX_SWITCH_TIME_SEC - MIN_SWITCH_TIME_SEC);
        switchInterval = seconds * 1000L;
        lastSwitchTime = System.currentTimeMillis();
        
        Logger.log("Manually Switched to Skill: " + currentSkill.getName());
    }
    
    // Internal weighted selection
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
        blackboard.reset();
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
            if (blackboard != null) blackboard.setForceStopSkill(false); // Reset flag
            pickNextSkill(currentSkill);
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

    @Override
    public void onExit() {
        WebFinder.getWebFinder().clearCustomNodes();
        Logger.log("Cleared custom web nodes.");
    }

    private void registerCustomNodes() {
        // Hill Giants
        new EdgevilleTrapdoorNode().register();
        new EdgevilleLadderNode().register();
        
        // Big Frogs
        new LumbridgeSwampEntranceNode().register();
        new LumbridgeSwampExitNode().register();
    }
}
