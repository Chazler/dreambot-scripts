package com.trainer.framework;

import org.dreambot.api.methods.skills.Skill;

public class Goal {
    public enum TrainingMode {
        DROP,
        BANK
    }

    private final Skill skill;
    private final int level;
    private final TrainingMode mode;

    public Goal(Skill skill, int level) {
        this(skill, level, TrainingMode.DROP); // Default to DROP
    }
    
    public Goal(Skill skill, int level, TrainingMode mode) {
        this.skill = skill;
        this.level = level;
        this.mode = mode;
    }

    public Skill getSkill() {
        return skill;
    }

    public int getLevel() {
        return level;
    }
    
    public TrainingMode getMode() {
        return mode;
    }

    @Override
    public String toString() {
        return skill.getName() + " -> " + level + " (" + mode + ")";
    }
}
