package com.combat_trainer.data;

import org.dreambot.api.methods.map.Area;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StaticLocationData {
    public static final List<LocationDef> ALL_LOCATIONS = new ArrayList<>();

    static {
        // Lumbridge Chickens
        ALL_LOCATIONS.add(new LocationDef(
            "Lumbridge Chickens",
            new Area(3225, 3295, 3235, 3300), 
            Arrays.asList("Chicken"),
            3,      // Rec Cmbt
            false,  // Aggr
            10,     // Safety
            2000,   // XP/hr
            3       // Max Def Needed
        ));

        // Lumbridge Cows
        ALL_LOCATIONS.add(new LocationDef(
            "Lumbridge Cows",
            new Area(3253, 3255, 3265, 3296), 
            Arrays.asList("Cow", "Cow calf"),
            6,
            false,
            9,
            5000,
            10      // Max Def Needed (User requested cap after 5, let's say 10 for safety)
        ));

        // Lumbridge Goblins
        ALL_LOCATIONS.add(new LocationDef(
            "Lumbridge Goblins",
            new Area(3240, 3240, 3260, 3255), 
            Arrays.asList("Goblin"),
            5,
            false,
            8,
            5000,
            10
        ));

        // Port Sarim Seagulls (North Dock)
        ALL_LOCATIONS.add(new LocationDef(
            "Port Sarim Seagulls",
            new Area(3026, 3240, 3030, 3225), 
            Arrays.asList("Seagull"),
            3,
            false,
            10,
            5000,
            1
        ));

        // Lumbridge Swamp Rats
        ALL_LOCATIONS.add(new LocationDef(
            "Lumbridge Rats",
            new Area(3235, 3195, 3252, 3205), // Entrance of swamp
            Arrays.asList("Giant rat"),
            5,
            false,
            8,
            3500,
            10
        ));
        
        // Barbarian Village (Long Hall)
        ALL_LOCATIONS.add(new LocationDef(
            "Barbarian Village",
            new Area(3078, 3415, 3086, 3426), 
            Arrays.asList("Barbarian"),
            25,
            false,
            7,
            10000,
            20
        ));
        
        // Al-Kharid Warriors
        ALL_LOCATIONS.add(new LocationDef(
            "Al-Kharid Palace",
            new Area(3283, 3160, 3303, 3177),
            Arrays.asList("Al-Kharid warrior"),
            35,
            true, // Aggressive
            6,
            15000,
            30
        ));
        
        // Varrock Guards
        ALL_LOCATIONS.add(new LocationDef(
            "Varrock Guards",
            new Area(3205, 3420, 3225, 3438), // Near castle
            Arrays.asList("Guard"),
            30,
            false,
            6,
            12000,
            40
        ));
        
        // Hill Giants (Edgeville Dungeon)
        ALL_LOCATIONS.add(new LocationDef(
            "Hill Giants (Edge)",
            new Area(3090, 9845, 3125, 9860),  // Dungeon area
            Arrays.asList("Hill Giant"),
            40,
            true,
            5,
            25000,
            50
        ));
    }
}
