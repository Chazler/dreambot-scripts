package com.allinone.skills.cooking.data;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;

public enum CookingLocation {
    AL_KHARID_RANGE(
        "Al Kharid Range",
        new Area(new Tile(3271, 3178, 0), new Tile(3275, 3182, 0)),
        new Area(new Tile(3268, 3164, 0), new Tile(3272, 3170, 0)),
        "Range",
        false
    ),
    LUMBRIDGE_RANGE(
        "Lumbridge Range",
        new Area(new Tile(3206, 3212, 0), new Tile(3212, 3217, 0)),
        new Area(new Tile(3207, 3217, 2), new Tile(3210, 3220, 2)),
        "Range",
        false
    ),
    EDGEVILLE_RANGE(
        "Edgeville Range",
        new Area(new Tile(3077, 3491, 0), new Tile(3081, 3496, 0)),
        new Area(new Tile(3091, 3488, 0), new Tile(3098, 3499, 0)),
        "Range",
        false
    ),
    ROGUES_DEN(
        "Rogues' Den",
        new Area(new Tile(3038, 4968, 1), new Tile(3046, 4975, 1)),
        new Area(new Tile(3038, 4968, 1), new Tile(3046, 4975, 1)),
        "Fire",
        true
    );

    private final String name;
    private final Area cookingArea;
    private final Area bankArea;
    private final String objectName;
    private final boolean isMembers;

    CookingLocation(String name, Area cookingArea, Area bankArea, String objectName, boolean isMembers) {
        this.name = name;
        this.cookingArea = cookingArea;
        this.bankArea = bankArea;
        this.objectName = objectName;
        this.isMembers = isMembers;
    }

    public String getLocationName() { return name; }
    public Area getCookingArea() { return cookingArea; }
    public Area getBankArea() { return bankArea; }
    public String getObjectName() { return objectName; }
    public boolean isMembers() { return isMembers; }
}
