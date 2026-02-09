package com.allinone.skills.fishing.data;

import org.dreambot.api.Client;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.methods.world.Worlds;

import java.util.ArrayList;
import java.util.List;

public class StaticFishingSpots {

    public static final List<FishingSpot> SPOTS = new ArrayList<>();

    static {
        // Shrimps - Draynor (F2P) - Level 1
        SPOTS.add(new FishingSpot("Fishing spot", new Area(3084, 3226, 3089, 3233), 
                  FishingMethod.NET, 1, false, false, null));

        // Trout/Salmon - Barbarian Village (F2P) - Level 20
        // Using Fly fishing rod + Feathers
        SPOTS.add(new FishingSpot("Rod Fishing spot", new Area(3100, 3422, 3110, 3435), 
                  FishingMethod.FLY, 20, false, false, "Feather"));

        // Lobsters - Karamja (F2P) - Level 40
        // Requires travelling, which might be tricky with just simple walking (boat needed)
        // Let's stick to simple walkable areas or safe spots first.
        // F2P Lobsters usually bank at Draynor or drop. Distant.
        // Let's add Cage Fishing at Catherby (Members)
        SPOTS.add(new FishingSpot("Cage Fishing spot", new Area(2835, 3428, 2860, 3434),
                  FishingMethod.CAGE, 40, true, true, null));
                  
        // Sharks - Fishing Guild (Members) - Level 76
        // Need guild access logic? Or just Catherby Sharks
        SPOTS.add(new FishingSpot("Net/Harpoon Fishing spot", new Area(2835, 3428, 2860, 3434),
                  FishingMethod.HARPOON, 76, true, true, null)); // Catherby Sharks
    }

    public static FishingSpot getBestSpot(int level) {
        World currentWorld = Worlds.getCurrent();
        boolean isMembersWorld = (currentWorld != null && currentWorld.isMembers()) || Client.isMembers();

        // 1. Filter available spots
        // 2. Sort by level requirement descending
        return SPOTS.stream()
                .filter(s -> level >= s.getLevelRequired())
                .filter(s -> !s.isMembers() || isMembersWorld)
                .reduce((first, second) -> second) // Get the last one in the list (highest level usually if added in order)
                .orElse(SPOTS.get(0));
    }
}
