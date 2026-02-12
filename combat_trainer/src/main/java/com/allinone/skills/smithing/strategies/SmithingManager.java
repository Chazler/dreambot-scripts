package com.allinone.skills.smithing.strategies;

import com.allinone.skills.smithing.data.SmithingItem;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class SmithingManager {
    
    private static SmithingItem manualOverride = null;
    private static final Set<SmithingItem> blacklistedItems = new HashSet<>();

    public static SmithingItem getBestItem() {
        if (manualOverride != null && !blacklistedItems.contains(manualOverride)) return manualOverride;

        int level = Skills.getRealLevel(Skill.SMITHING);
        
        return Arrays.stream(SmithingItem.values())
                .filter(i -> !blacklistedItems.contains(i))
                .filter(i -> i.getLevelRequired() <= level)
                .sorted(Comparator.comparingInt(SmithingItem::getLevelRequired).reversed())
                .findFirst()
                .orElse(null); // Return null if nothing found
    }
    
    public static void setManualOverride(SmithingItem item) {
        manualOverride = item;
    }
    
    public static void blacklist(SmithingItem item) {
        blacklistedItems.add(item);
    }
    
    public static void blacklistType(com.allinone.skills.smithing.data.SmithingType type) {
        for (SmithingItem item : SmithingItem.values()) {
            if (item.getType() == type) {
                blacklistedItems.add(item);
            }
        }
    }
    
    public static void clearBlacklist() {
        blacklistedItems.clear();
    }

    public static com.allinone.skills.smithing.data.SmithingLocation getBestLocation(com.allinone.skills.smithing.data.SmithingType type) {
        // Simple logic for now:
        // Use Al Kharid for Smelting (F2P/P2P accessible, decent)
        // Use Varrock West for Anvil (Standard)
        
        if (type == com.allinone.skills.smithing.data.SmithingType.SMELTING) {
             return com.allinone.skills.smithing.data.SmithingLocation.AL_KHARID_FURNACE;
        } else {
             return com.allinone.skills.smithing.data.SmithingLocation.VARROCK_WEST_ANVIL;
        }
    }
}
