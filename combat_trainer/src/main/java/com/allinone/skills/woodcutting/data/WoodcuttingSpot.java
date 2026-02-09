package com.allinone.skills.woodcutting.data;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;

public class WoodcuttingSpot {
    private final String name;
    private final Area area;
    private final TreeType treeType;
    private final boolean shouldBank;
    private final Area bankArea; // Optional, if specific bank needed, otherwise use API default
    private final boolean isMembers;
    
    public WoodcuttingSpot(String name, Area area, TreeType treeType, boolean shouldBank, Area bankArea, boolean isMembers) {
        this.name = name;
        this.area = area;
        this.treeType = treeType;
        this.shouldBank = shouldBank;
        this.bankArea = bankArea;
        this.isMembers = isMembers;
    }

    public String getName() {
        return name;
    }

    public Area getArea() {
        return area;
    }

    public TreeType getTreeType() {
        return treeType;
    }

    public boolean shouldBank() {
        return shouldBank;
    }

    public boolean isMembers() {
        return isMembers;
    }
}
