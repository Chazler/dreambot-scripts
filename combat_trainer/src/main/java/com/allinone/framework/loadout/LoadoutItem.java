package com.allinone.framework.loadout;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LoadoutItem {

    private final String name;
    private final Map<Skill, Integer> requirements;

    public LoadoutItem(String name) {
        this.name = name;
        this.requirements = new HashMap<>();
    }

    // Builder pattern
    public LoadoutItem req(Skill skill, int level) {
        requirements.put(skill, level);
        return this;
    }

    public String getName() {
        return name;
    }

    public Map<Skill, Integer> getRequirements() {
        return Collections.unmodifiableMap(requirements);
    }

    public boolean meetsRequirements() {
        for (Map.Entry<Skill, Integer> entry : requirements.entrySet()) {
            if (Skills.getRealLevel(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
