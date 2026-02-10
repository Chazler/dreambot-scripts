package com.allinone.framework.loadout;

public class FishingLoadout extends Loadout {
    
    // Fishing is specific (you need a specific tool), so we pass it in.
    public FishingLoadout(String toolName, String baitName, int baitAmount) {
        if (toolName != null) {
            addInventoryItem(toolName);
        }
        if (baitName != null && baitAmount > 0) {
            addInventoryItem(baitName, baitAmount);
        }
    }

    public FishingLoadout(String toolName) {
        this(toolName, null, 0);
    }
}
