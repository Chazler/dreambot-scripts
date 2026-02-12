package com.allinone.skills.magic.data;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;

/**
 * Locations suitable for magic combat training.
 * Uses the same NPC locations as melee/ranged combat.
 */
public enum MagicTrainingLocation {
    CHICKENS_LUMBRIDGE(
        "Chickens (Lumbridge)",
        new Area(new Tile(3225, 3295, 0), new Tile(3238, 3301, 0)),
        new String[]{"Chicken"},
        1, false
    ),
    COWS_LUMBRIDGE(
        "Cows (Lumbridge)",
        new Area(new Tile(3242, 3255, 0), new Tile(3265, 3296, 0)),
        new String[]{"Cow", "Cow calf"},
        1, false
    ),
    GOBLINS_LUMBRIDGE(
        "Goblins (Lumbridge)",
        new Area(new Tile(3244, 3224, 0), new Tile(3260, 3245, 0)),
        new String[]{"Goblin"},
        1, false
    ),
    ROCK_CRABS(
        "Rock Crabs (Rellekka)",
        new Area(new Tile(2689, 3711, 0), new Tile(2727, 3733, 0)),
        new String[]{"Rock Crab"},
        30, true
    );

    private final String name;
    private final Area area;
    private final String[] npcNames;
    private final int recommendedLevel;
    private final boolean isMembers;

    MagicTrainingLocation(String name, Area area, String[] npcNames, int recommendedLevel, boolean isMembers) {
        this.name = name;
        this.area = area;
        this.npcNames = npcNames;
        this.recommendedLevel = recommendedLevel;
        this.isMembers = isMembers;
    }

    public String getName() { return name; }
    public Area getArea() { return area; }
    public String[] getNpcNames() { return npcNames; }
    public int getRecommendedLevel() { return recommendedLevel; }
    public boolean isMembers() { return isMembers; }
}
