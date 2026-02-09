package com.allinone.skills.smithing.strategies;

import com.allinone.skills.smithing.data.SmithingItem;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SmithingManager {
    
    private static SmithingItem manualOverride = null;

    public static SmithingItem getBestItem() {
        if (manualOverride != null) return manualOverride;

        int level = Skills.getRealLevel(Skill.SMITHING);
        
        // Simple logic: return the highest level item we can make
        // In a real bot, we would check bank contents to see what supplies we have
        // For now, we assume we want to do the highest level content
        
        // Prioritize Forging if we have bars? No, simpler to just pick best possible by level.
        // Actually, without checking bank, we might pick something we can't make.
        // Let's stick to a safe default progression or let the strategy be:
        // "Best item I have materials for, otherwise Best item by level"
        
        return Arrays.stream(SmithingItem.values())
                .filter(i -> i.getLevelRequired() <= level)
                .sorted(Comparator.comparingInt(SmithingItem::getLevelRequired).reversed())
                .findFirst()
                .orElse(SmithingItem.BRONZE_BAR);
    }
    
    public static void setManualOverride(SmithingItem item) {
        manualOverride = item;
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
