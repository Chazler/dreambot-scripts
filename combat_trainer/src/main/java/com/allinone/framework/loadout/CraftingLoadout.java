package com.allinone.framework.loadout;

public class CraftingLoadout extends Loadout {

    public CraftingLoadout(String toolName, String primaryItem, int primaryAmount,
                           String secondaryItem, int secondaryAmount) {
        // Equipment from static data: add setSlot() calls here
        // when StaticCraftingGearData gets items.

        // Runtime: tool + materials
        if (toolName != null) addInventoryItem(toolName);
        if (primaryItem != null && primaryAmount > 0) addInventoryItem(primaryItem, primaryAmount);
        if (secondaryItem != null && secondaryAmount > 0) addInventoryItem(secondaryItem, secondaryAmount);
    }
}
