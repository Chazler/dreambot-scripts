package com.allinone.skills.woodcutting.data;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StaticWoodcuttingSpots {
    
    public static final List<WoodcuttingSpot> SPOTS = new ArrayList<>();
    
    static {
        // Normal Trees - Lumbridge (F2P)
        SPOTS.add(new WoodcuttingSpot("Lumbridge Trees", new Area(3186, 3236, 3208, 3254), TreeType.NORMAL, true, null, false));
        // Normal Trees - Varrock West (F2P)
        SPOTS.add(new WoodcuttingSpot("Varrock West Trees", new Area(3156, 3406, 3173, 3426), TreeType.NORMAL, true, null, false));
        // Normal Trees - Draynor North (F2P)
        SPOTS.add(new WoodcuttingSpot("Draynor North Trees", new Area(3106, 3266, 3125, 3287), TreeType.NORMAL, true, null, false));
        // Normal Trees - Falador North (F2P)
        SPOTS.add(new WoodcuttingSpot("Falador North Trees", new Area(2998, 3381, 3030, 3390), TreeType.NORMAL, true, null, false));
        
        // Oak - Draynor / Lumbridge West (F2P)
        SPOTS.add(new WoodcuttingSpot("Draynor Oaks", new Area(3103, 3240, 3113, 3249), TreeType.OAK, true, null, false));
        // Oak - Varrock West (F2P)
        SPOTS.add(new WoodcuttingSpot("Varrock West Oaks", new Area(3161, 3410, 3170, 3422), TreeType.OAK, true, null, false));
        // Oak - Falador West (F2P)
        SPOTS.add(new WoodcuttingSpot("Falador West Oaks", new Area(2905, 3360, 2925, 3370), TreeType.OAK, true, null, false));
        
        // Willow - Draynor (F2P)
        SPOTS.add(new WoodcuttingSpot("Draynor Willows", new Area(3081, 3227, 3091, 3238), TreeType.WILLOW, true, null, false));
         // Willow - Port Sarim (F2P)
        SPOTS.add(new WoodcuttingSpot("Port Sarim Willows", new Area(3056, 3251, 3064, 3257), TreeType.WILLOW, true, null, false));
        // Willow - Seers Village (Members)
        SPOTS.add(new WoodcuttingSpot("Seers Willows", new Area(2708, 3470, 2718, 3485), TreeType.WILLOW, true, null, true));
        
        // Maple - Seers (Members)
        SPOTS.add(new WoodcuttingSpot("Seers Maples", new Area(2717, 3498, 2735, 3503), TreeType.MAPLE, true, null, true));
        
        // Yew - Edgeville / Varrock Palace (F2P)
        SPOTS.add(new WoodcuttingSpot("Edgeville Yews", new Area(3085, 3468, 3089, 3482), TreeType.YEW, true, null, false));
        SPOTS.add(new WoodcuttingSpot("Varrock Palace Yews", new Area(3203, 3500, 3223, 3506), TreeType.YEW, true, null, false));
        // Yew - Falador South (F2P)
        SPOTS.add(new WoodcuttingSpot("Falador South Yews", new Area(2994, 3310, 3020, 3328), TreeType.YEW, true, null, false));
        // Yew - Rimmington / Port Sarim (F2P)
        SPOTS.add(new WoodcuttingSpot("Port Sarim Yews", new Area(2924, 3223, 2942, 3237), TreeType.YEW, true, null, false));
        // Yew - Seers Church (Members)
        SPOTS.add(new WoodcuttingSpot("Seers Church Yews", new Area(2705, 3458, 2716, 3465), TreeType.YEW, true, null, true));
        
        // Magic - Seers (Members)
        SPOTS.add(new WoodcuttingSpot("Seers Magics", new Area(2690, 3422, 2697, 3427), TreeType.MAGIC, true, null, true));
    }
    
    public StaticWoodcuttingSpots() {}

    public static TreeType getBestTreeType(int level) {
        World currentWorld = Worlds.getCurrent();
        boolean isMembersWorld = (currentWorld != null && currentWorld.isMembers()) || Client.isMembers();

        if (level >= 75 && isMembersWorld) return TreeType.MAGIC;
        if (level >= 60) return TreeType.YEW;
        if (level >= 45 && isMembersWorld) return TreeType.MAPLE;
        if (level >= 30) return TreeType.WILLOW;
        if (level >= 15) return TreeType.OAK;
        return TreeType.NORMAL;
    }
    
    public static List<WoodcuttingSpot> getSpots(TreeType type) {
        World currentWorld = Worlds.getCurrent();
        boolean isMembersWorld = (currentWorld != null && currentWorld.isMembers()) || Client.isMembers();
        
        return SPOTS.stream()
                .filter(s -> s.getTreeType() == type)
                .filter(s -> !s.isMembers() || isMembersWorld)
                .collect(Collectors.toList());
    }

    public static WoodcuttingSpot getBestSpot(int level) {
        TreeType targetType = getBestTreeType(level);
        List<WoodcuttingSpot> candidates = getSpots(targetType);

        if (candidates.isEmpty()) {
            return SPOTS.get(0);
        }
        
        // Return a random spot from the candidates
        return candidates.get(new java.util.Random().nextInt(candidates.size()));
    }
}
