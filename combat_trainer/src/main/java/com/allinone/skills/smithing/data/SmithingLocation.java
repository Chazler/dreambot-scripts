package com.allinone.skills.smithing.data;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;

public enum SmithingLocation {
    
    // Anvils
    VARROCK_WEST_ANVIL(
            SmithingType.FORGING,
            new Area(3185, 3427, 3189, 3424), // Anvil Area
            new Area(3182, 3446, 3186, 3434)  // Varrock West Bank Area
    ),
    
    // Furnaces
    AL_KHARID_FURNACE(
            SmithingType.SMELTING,
            new Area(3272, 3188, 3279, 3184), // Furnace Area
            new Area(3268, 3171, 3272, 3164)  // Al Kharid Bank Area
    ),
    EDGEVILLE_FURNACE(
            SmithingType.SMELTING,
            new Area(3105, 3501, 3110, 3496), // Furnace Area
            new Area(3091, 3493, 3098, 3488)  // Edgeville Bank Area (Common)
    ),
    FALADOR_FURNACE(
            SmithingType.SMELTING,
            new Area(2970, 3373, 2976, 3369), // Furnace Area
            new Area(2943, 3368, 2949, 3374)  // Falador West Bank
    );

    private final SmithingType type;
    private final Area workArea;
    private final Area bankArea;

    SmithingLocation(SmithingType type, Area workArea, Area bankArea) {
        this.type = type;
        this.workArea = workArea;
        this.bankArea = bankArea;
    }

    public SmithingType getType() {
        return type;
    }

    public Area getWorkArea() {
        return workArea;
    }

    public Area getBankArea() {
        return bankArea;
    }
}
