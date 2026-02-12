package com.allinone.skills.ranged.data;

import org.dreambot.api.Client;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.world.Worlds;

import java.util.Arrays;
import java.util.Comparator;

public class StaticRangedLocations {

    /**
     * Returns the best training location for the current ranged level.
     */
    public static RangedTrainingLocation getBestLocation() {
        int level = Skills.getRealLevel(Skill.RANGED);
        boolean members = (Worlds.getCurrent() != null && Worlds.getCurrent().isMembers()) || Client.isMembers();

        return Arrays.stream(RangedTrainingLocation.values())
                .filter(loc -> !loc.isMembers() || members)
                .filter(loc -> level >= loc.getMinLevel())
                .filter(loc -> level <= loc.getMaxLevel())
                .sorted(Comparator.comparingInt(RangedTrainingLocation::getMinLevel).reversed())
                .findFirst()
                .orElse(RangedTrainingLocation.CHICKENS_LUMBRIDGE);
    }
}
