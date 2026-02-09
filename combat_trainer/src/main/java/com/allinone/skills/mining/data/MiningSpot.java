package com.allinone.skills.mining.data;

import org.dreambot.api.methods.map.Area;

public class MiningSpot {
    private final String name;
    private final Area area;
    private final RockType rockType;
    private final boolean isMembers;
    private final boolean shouldBank;

    public MiningSpot(String name, Area area, RockType rockType, boolean isMembers, boolean shouldBank) {
        this.name = name;
        this.area = area;
        this.rockType = rockType;
        this.isMembers = isMembers;
        this.shouldBank = shouldBank;
    }

    public String getName() { return name; }
    public Area getArea() { return area; }
    public RockType getRockType() { return rockType; }
    public boolean isMembers() { return isMembers; }
    public boolean shouldBank() { return shouldBank; }
}
