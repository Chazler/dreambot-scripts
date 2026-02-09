package com.trainer.framework;

import org.dreambot.api.methods.skills.Skills;
import java.util.LinkedList;
import java.util.Queue;

public class GoalManager {
    private final Queue<Goal> goals = new LinkedList<>();
    private Goal currentGoal;

    public void addGoal(Goal goal) {
        goals.add(goal);
    }

    public Goal getCurrentGoal() {
        if (currentGoal == null) {
            nextGoal();
        }
        
        // Check if current goal is complete
        if (currentGoal != null && Skills.getRealLevel(currentGoal.getSkill()) >= currentGoal.getLevel()) {
            nextGoal();
        }
        
        return currentGoal;
    }

    private void nextGoal() {
        currentGoal = goals.poll();
    }
    
    public void deferCurrentGoal() {
        if (currentGoal != null) {
            goals.add(currentGoal);
            currentGoal = null;
        }
    }
    
    public String getCurrentGoalString() {
        Goal g = getCurrentGoal();
        return g != null ? g.toString() : "All Goals Completed";
    }
}
