package com.allinone.skills.cooking.strategies;

import com.allinone.skills.cooking.data.CookingItem;
import com.allinone.skills.cooking.data.CookingLocation;
import org.dreambot.api.Client;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.world.Worlds;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class CookingManager {

    private static final Set<CookingItem> blacklistedItems = new HashSet<>();

    public static CookingItem getBestItem() {
        int level = Skills.getRealLevel(Skill.COOKING);

        return Arrays.stream(CookingItem.values())
                .filter(i -> !blacklistedItems.contains(i))
                .filter(i -> i.getLevelRequired() <= level)
                .sorted(Comparator.comparingInt(CookingItem::getLevelRequired).reversed())
                .findFirst()
                .orElse(null);
    }

    public static CookingLocation getBestLocation() {
        boolean members = (Worlds.getCurrent() != null && Worlds.getCurrent().isMembers()) || Client.isMembers();

        if (members) {
            return CookingLocation.ROGUES_DEN;
        }
        return CookingLocation.AL_KHARID_RANGE;
    }

    public static void blacklist(CookingItem item) {
        blacklistedItems.add(item);
    }

    public static void clearBlacklist() {
        blacklistedItems.clear();
    }
}
