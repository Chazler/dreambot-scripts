package com.allinone.skills.magic.strategies;

import com.allinone.skills.magic.data.MagicSpell;
import com.allinone.skills.magic.data.MagicTrainingLocation;
import org.dreambot.api.Client;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.world.Worlds;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class MagicManager {

    private static final Set<MagicSpell> blacklistedSpells = new HashSet<>();

    /**
     * Returns the best combat spell for the current level.
     * Prefers combat spells for training (XP comes from both cast + damage).
     */
    public static MagicSpell getBestCombatSpell() {
        int level = Skills.getRealLevel(Skill.MAGIC);

        return Arrays.stream(MagicSpell.values())
                .filter(s -> !blacklistedSpells.contains(s))
                .filter(s -> s.getLevelRequired() <= level)
                .filter(MagicSpell::isCombatSpell)
                .sorted(Comparator.comparingInt(MagicSpell::getLevelRequired).reversed())
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns the best overall spell, including High Alchemy.
     */
    public static MagicSpell getBestSpell() {
        int level = Skills.getRealLevel(Skill.MAGIC);

        return Arrays.stream(MagicSpell.values())
                .filter(s -> !blacklistedSpells.contains(s))
                .filter(s -> s.getLevelRequired() <= level)
                .sorted(Comparator.comparingInt(MagicSpell::getLevelRequired).reversed())
                .findFirst()
                .orElse(null);
    }

    public static MagicTrainingLocation getBestLocation() {
        boolean members = (Worlds.getCurrent() != null && Worlds.getCurrent().isMembers()) || Client.isMembers();

        if (members) {
            return MagicTrainingLocation.ROCK_CRABS;
        }
        return MagicTrainingLocation.COWS_LUMBRIDGE;
    }

    public static void blacklist(MagicSpell spell) {
        blacklistedSpells.add(spell);
    }

    public static void clearBlacklist() {
        blacklistedSpells.clear();
    }
}
