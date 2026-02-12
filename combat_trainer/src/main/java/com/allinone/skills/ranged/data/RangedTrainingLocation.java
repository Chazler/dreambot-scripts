package com.allinone.skills.ranged.data;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;

/**
 * Locations for ranged combat training.
 */
public enum RangedTrainingLocation {
    CHICKENS_LUMBRIDGE(
        "Chickens (Lumbridge)",
        new Area(new Tile(3225, 3295, 0), new Tile(3238, 3301, 0)),
        new String[]{"Chicken"},
        1, 15, false
    ),
    COWS_LUMBRIDGE(
        "Cows (Lumbridge)",
        new Area(new Tile(3242, 3255, 0), new Tile(3265, 3296, 0)),
        new String[]{"Cow", "Cow calf"},
        1, 30, false
    ),
    MINOTAURS(
        "Minotaurs (Stronghold)",
        new Area(new Tile(1856, 5213, 0), new Tile(1876, 5232, 0)),
        new String[]{"Minotaur"},
        1, 50, false
    ),
    HILL_GIANTS(
        "Hill Giants (Edgeville Dungeon)",
        new Area(new Tile(3095, 9826, 0), new Tile(3126, 9856, 0)),
        new String[]{"Hill Giant"},
        25, 80, false
    ),
    ROCK_CRABS(
        "Rock Crabs (Rellekka)",
        new Area(new Tile(2689, 3711, 0), new Tile(2727, 3733, 0)),
        new String[]{"Rock Crab"},
        1, 99, true
    ),
    SAND_CRABS(
        "Sand Crabs (Hosidius)",
        new Area(new Tile(1726, 3462, 0), new Tile(1782, 3478, 0)),
        new String[]{"Sand Crab"},
        1, 99, true
    );

    private final String name;
    private final Area area;
    private final String[] npcNames;
    private final int minLevel;
    private final int maxLevel;
    private final boolean isMembers;

    RangedTrainingLocation(String name, Area area, String[] npcNames, int minLevel, int maxLevel, boolean isMembers) {
        this.name = name;
        this.area = area;
        this.npcNames = npcNames;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.isMembers = isMembers;
    }

    public String getName() { return name; }
    public Area getArea() { return area; }
    public String[] getNpcNames() { return npcNames; }
    public int getMinLevel() { return minLevel; }
    public int getMaxLevel() { return maxLevel; }
    public boolean isMembers() { return isMembers; }
}
