package com.allinone.skills.fishing.data;

import org.dreambot.api.methods.map.Area;

public class FishingSpot {
    private final String npcName; // Fishing spots are NPCs in OSRS
    private final Area area;
    private final FishingMethod method;
    private final int levelRequired;
    private final boolean shouldBank;
    private final boolean isMembers;
    private final String baitItem; // Optional, null if none
    private final double estimatedXp;

    public FishingSpot(String npcName, Area area, FishingMethod method, int levelRequired, boolean shouldBank, boolean isMembers, String baitItem) {
        this(npcName, area, method, levelRequired, shouldBank, isMembers, baitItem, 10.0);
    }
    
    public FishingSpot(String npcName, Area area, FishingMethod method, int levelRequired, boolean shouldBank, boolean isMembers, String baitItem, double estimatedXp) {
        this.npcName = npcName;
        this.area = area;
        this.method = method;
        this.levelRequired = levelRequired;
        this.shouldBank = shouldBank;
        this.isMembers = isMembers;
        this.baitItem = baitItem;
        this.estimatedXp = estimatedXp;
    }

    public String getNpcName() { return npcName; }
    public Area getArea() { return area; }
    public FishingMethod getMethod() { return method; }
    public int getLevelRequired() { return levelRequired; }
    public boolean shouldBank() { return shouldBank; }
    public boolean isMembers() { return isMembers; }
    public String getBaitItem() { return baitItem; }
    public double getEstimatedXp() { return estimatedXp; }
}
