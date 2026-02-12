package com.allinone.framework.loadout;

import com.allinone.skills.cooking.data.StaticCookingGearData;

public class CookingLoadout extends Loadout {

    public CookingLoadout(String rawFoodName) {
        // Equipment from static data
        setSlot(Slot.HANDS, StaticCookingGearData.HANDS);

        // Runtime: raw food
        if (rawFoodName != null) {
            addInventoryItem(rawFoodName, 28);
        }
    }
}
