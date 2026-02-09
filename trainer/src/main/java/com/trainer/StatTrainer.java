package com.trainer;

import com.trainer.antiban.Antiban;
import com.trainer.framework.Goal;
import com.trainer.framework.GoalManager;
import com.trainer.framework.Task;
import com.trainer.tasks.*;
import org.dreambot.api.Client;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

@ScriptManifest(
    author = "Copilot",
    description = "AIO trainer v5.0 - Safety & Economy Update",
    category = Category.MISC,
    version = 5.1,
    name = "Simple Stat Trainer"
)
public class StatTrainer extends AbstractScript {

    private List<Task> tasks = new ArrayList<>();
    private Antiban antiban;
    private GoalManager goalManager;
    
    private String currentStatus = "Initializing...";
    private long startTime;
    
    // XP Tracking
    private Skill currentSkill = null;
    private int startSkillXp = 0;
    private long skillStartTime = 0;

    @Override
    public void onStart() {
        startTime = System.currentTimeMillis();
        antiban = new Antiban(this);
        goalManager = new GoalManager();
        
        setupGoals();
        
        // Add tasks in priority order
        tasks.add(new EatTask(this)); // Survival first
        tasks.add(new CombatTask(this, antiban)); // Combat logic
        tasks.add(new EquipTask(this)); // Gear upgrades
        tasks.add(new BankTask(this, goalManager)); // Banking before dropping
        tasks.add(new LootTask(this)); // Looting
        
        // Activity Tasks
        tasks.add(new WoodcuttingTask(this, goalManager));
        tasks.add(new MiningTask(this, goalManager));
        tasks.add(new FishingTask(this, goalManager));
        tasks.add(new CombatTrainingTask(this, goalManager));
        
        log("Trainer initialized with " + tasks.size() + " tasks.");
    }

    private void setupGoals() {
        // --- DEFINE YOUR GOALS HERE ---
        
        // 1. Early Game (Power level) - Shuffled for randomization
        List<Goal> earlyGoals = new ArrayList<>();
        earlyGoals.add(new Goal(Skill.ATTACK, 5));
        earlyGoals.add(new Goal(Skill.STRENGTH, 5));
        earlyGoals.add(new Goal(Skill.WOODCUTTING, 10, Goal.TrainingMode.DROP)); 
        earlyGoals.add(new Goal(Skill.MINING, 5, Goal.TrainingMode.DROP));       
        earlyGoals.add(new Goal(Skill.FISHING, 5, Goal.TrainingMode.DROP));      

        java.util.Collections.shuffle(earlyGoals);
        for (Goal g : earlyGoals) goalManager.addGoal(g);
        
        // 2. Mid Game (Banking for Economy) - Shuffled
        List<Goal> midGoals = new ArrayList<>();
        midGoals.add(new Goal(Skill.DEFENCE, 10));
        midGoals.add(new Goal(Skill.WOODCUTTING, 20, Goal.TrainingMode.BANK));
        midGoals.add(new Goal(Skill.MINING, 20, Goal.TrainingMode.BANK));
        midGoals.add(new Goal(Skill.FISHING, 20, Goal.TrainingMode.BANK));

        java.util.Collections.shuffle(midGoals);
        for (Goal g : midGoals) goalManager.addGoal(g);
        
        // Final
        goalManager.addGoal(new Goal(Skill.STRENGTH, 99));
    }

    @Override
    public int onLoop() {
        if (!Client.isLoggedIn()) {
            return 1000;
        }

        // XP Tracker Update
        if (goalManager != null) {
            Goal goal = goalManager.getCurrentGoal();
            if (goal != null) {
                Skill skill = goal.getSkill();
                if (skill != currentSkill) {
                    currentSkill = skill;
                    startSkillXp = Skills.getExperience(skill);
                    skillStartTime = System.currentTimeMillis();
                }
            }
        }

        for (Task task : tasks) {
            if (task.accept()) {
                currentStatus = task.status();
                return task.execute();
            }
        }
        
        currentStatus = "Idle (No task active)";
        return 600;
    }

    @Override
    public void onPaint(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(10, 10, 200, 150);
        
        g.setColor(Color.WHITE);
        g.drawRect(10, 10, 200, 150);
        
        g.setColor(Color.CYAN);
        g.drawString("Simple Stat Trainer v5.0", 20, 30);
        
        g.setColor(Color.WHITE);
        g.drawString("Status: " + currentStatus, 20, 50);
        g.drawString("Runtime: " + getRuntimeFormatted(), 20, 65);
        
        if (goalManager != null) {
            Goal goal = goalManager.getCurrentGoal();
            String goalStr = (goal != null) ? goal.toString() : "None";
            g.drawString("Goal: " + goalStr, 20, 80);
        }
        
        if (currentSkill != null) {
            long timeRunning = System.currentTimeMillis() - skillStartTime;
            int currentXp = Skills.getExperience(currentSkill);
            int gainedXp = currentXp - startSkillXp;
            int xpPerHour = (int) (timeRunning > 0 ? gainedXp * 3600000.0 / timeRunning : 0);
            
            g.setColor(Color.GREEN);
            g.drawString("Skill: " + currentSkill.getName() + " (Lvl " + Skills.getRealLevel(currentSkill) + ")", 20, 100);
            g.drawString("XP Gained: " + gainedXp, 20, 115);
            g.drawString("XP/Hr: " + xpPerHour, 20, 130);
        }
        
        g.setColor(Color.YELLOW);
        g.drawString("Safety & Economy Update", 20, 145);
    }
    
    private String getRuntimeFormatted() {
        long millis = System.currentTimeMillis() - startTime;
        long second = (millis / 1000) % 60;
        long minute = (millis / (1000 * 60)) % 60;
        long hour = (millis / (1000 * 60 * 60)) % 24;
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }
}
