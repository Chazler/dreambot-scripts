package com.allinone.skills.combat.data;

import org.dreambot.api.methods.map.Area;
import java.util.List;

public class LocationDef {
    private final String name;
    private final Area area;
    private final List<String> npcNames;
    private final int recommendedCombatLevel;
    private final boolean isAggressive;
    private final int safetyRating; // 1-10, 10 being safest
    private final int expectedXpPerHour; 
    private final int maxDefenceNeeded;

    public LocationDef(String name, Area area, List<String> npcNames, int recommendedCombatLevel, 
                       boolean isAggressive, int safetyRating, int expectedXpPerHour, int maxDefenceNeeded) {
        this.name = name;
        this.area = area;
        this.npcNames = npcNames;
        this.recommendedCombatLevel = recommendedCombatLevel;
        this.isAggressive = isAggressive;
        this.safetyRating = safetyRating;
        this.expectedXpPerHour = expectedXpPerHour;
        this.maxDefenceNeeded = maxDefenceNeeded;
    }

    public String getName() { return name; }
    public Area getArea() { return area; }
    public List<String> getNpcNames() { return npcNames; }
    public int getRecommendedCombatLevel() { return recommendedCombatLevel; }
    public boolean isAggressive() { return isAggressive; }
    public int getSafetyRating() { return safetyRating; }
    public int getExpectedXpPerHour() { return expectedXpPerHour; }
    public int getMaxDefenceNeeded() { return maxDefenceNeeded; }
}
