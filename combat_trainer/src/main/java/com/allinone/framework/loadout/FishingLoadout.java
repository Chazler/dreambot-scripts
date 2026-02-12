package com.allinone.framework.loadout;

public class FishingLoadout extends Loadout {

    public FishingLoadout(String toolName, String baitName, int baitAmount) {
        // Equipment from static data: add setSlot() calls here
        // when StaticFishingGearData gets items (e.g. Angler outfit).

        // Runtime: tool + bait
        if (toolName != null) addInventoryItem(toolName);
        if (baitName != null && baitAmount > 0) addInventoryItem(baitName, baitAmount);
    }

    public FishingLoadout(String toolName) {
        this(toolName, null, 0);
    }
}
