package com.allinone.skills.crafting.strategies;

import com.allinone.skills.crafting.data.CraftingItem;
import com.allinone.skills.crafting.data.CraftingType;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class CraftingManager {

    private static final Set<CraftingItem> blacklistedItems = new HashSet<>();

    /**
     * Returns the best craftable item for the current level.
     * Prioritizes higher-level items for better XP.
     */
    public static CraftingItem getBestItem() {
        int level = Skills.getRealLevel(Skill.CRAFTING);

        return Arrays.stream(CraftingItem.values())
                .filter(i -> !blacklistedItems.contains(i))
                .filter(i -> i.getLevelRequired() <= level)
                .sorted(Comparator.comparingInt(CraftingItem::getLevelRequired).reversed())
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns the best item of a specific crafting type.
     */
    public static CraftingItem getBestItemOfType(CraftingType type) {
        int level = Skills.getRealLevel(Skill.CRAFTING);

        return Arrays.stream(CraftingItem.values())
                .filter(i -> !blacklistedItems.contains(i))
                .filter(i -> i.getLevelRequired() <= level)
                .filter(i -> i.getType() == type)
                .sorted(Comparator.comparingInt(CraftingItem::getLevelRequired).reversed())
                .findFirst()
                .orElse(null);
    }

    public static void blacklist(CraftingItem item) {
        blacklistedItems.add(item);
    }

    public static void clearBlacklist() {
        blacklistedItems.clear();
    }
}
