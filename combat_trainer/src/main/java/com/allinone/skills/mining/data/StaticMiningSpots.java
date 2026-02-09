package com.allinone.skills.mining.data;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.Client;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;
import org.dreambot.api.methods.interactive.Players;

public class StaticMiningSpots {
    public static final List<MiningSpot> SPOTS = new ArrayList<>();
    
    static {
        // Copper - Varrock East (F2P)
        SPOTS.add(new MiningSpot("Varrock East Copper", new Area(3280, 3360, 3290, 3370), RockType.COPPER, false, true));
        // Tin - Varrock East (F2P)
        SPOTS.add(new MiningSpot("Varrock East Tin", new Area(3278, 3361, 3289, 3371), RockType.TIN, false, true));
        
        // Copper - Rimmington
        SPOTS.add(new MiningSpot("Rimmington Copper", new Area(2974, 3244, 2984, 3251), RockType.COPPER, false, true));
        
        // Clay - Rimmington (F2P)
        SPOTS.add(new MiningSpot("Rimmington Clay", new Area(2984, 3236, 2989, 3243), RockType.CLAY, false, true));
        
        // Iron - Varrock East (F2P) - SW part of mine
        SPOTS.add(new MiningSpot("Varrock East Iron", new Area(3284, 3367, 3289, 3371), RockType.IRON, false, true));
        // Iron - Al Kharid (F2P)
        SPOTS.add(new MiningSpot("Al Kharid Iron", new Area(3301, 3301, 3306, 3307), RockType.IRON, false, true));

        // Silver - Al Kharid
        SPOTS.add(new MiningSpot("Al Kharid Silver", new Area(3293, 3314, 3303, 3317), RockType.SILVER, false, true));
        
        // Coal - Barbarian Village (F2P)
        SPOTS.add(new MiningSpot("Barbarian Village Coal", new Area(3080, 3418, 3086, 3422), RockType.COAL, false, true));
        
        // Gold - Crafting Guild (F2P)
        SPOTS.add(new MiningSpot("Crafting Guild Gold", new Area(2938, 3278, 2943, 3283), RockType.GOLD, false, true));
        
        // Mithril - Al Kharid
        SPOTS.add(new MiningSpot("Al Kharid Mithril", new Area(3301, 3300, 3308, 3306), RockType.MITHRIL, false, true));
        
        // Adamantite - Lumbridge Swamp West / Draynor/Zanaris shed nearby?
        SPOTS.add(new MiningSpot("Lumbridge Swamp Adamant", new Area(3226, 3145, 3230, 3149), RockType.ADAMANTITE, false, true));
    }
    
    public static RockType getBestRockType(int level) {
        World currentWorld = Worlds.getCurrent();
        boolean isMembersWorld = (currentWorld != null && currentWorld.isMembers()) || Client.isMembers();
        
        // Progression
        if (level >= 85 && isMembersWorld) return RockType.RUNITE;
        if (level >= 70) return RockType.ADAMANTITE;
        if (level >= 55) return RockType.MITHRIL;
        if (level >= 40) return RockType.GOLD; // Ore price is good, XP OK
        if (level >= 30) return RockType.COAL;
        if (level >= 20) return RockType.SILVER; // or Iron. Iron is best XP but assuming generic progression
        if (level >= 15) return RockType.IRON; // Powermine iron is best, but we are banking
        if (level >= 1) return RockType.TIN; // or Copper

        return RockType.TIN;
    }

    public static List<MiningSpot> getSpots(RockType type) {
        World currentWorld = Worlds.getCurrent();
        boolean isMembersWorld = (currentWorld != null && currentWorld.isMembers()) || Client.isMembers();
        
        return SPOTS.stream()
                .filter(s -> s.getRockType() == type)
                .filter(s -> !s.isMembers() || isMembersWorld)
                .collect(Collectors.toList());
    }
}
