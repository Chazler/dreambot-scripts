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
        SPOTS.add(new WoodcuttingSpot("Lumbridge Trees", new Area(3186, 3236, 3208, 3254), TreeType.NORMAL, false, null, false));
        
        // Oak - Draynor / Lumbridge West (F2P)
        SPOTS.add(new WoodcuttingSpot("Draynor Oaks", new Area(3103, 3240, 3113, 3249), TreeType.OAK, true, null, false));
        
        // Willow - Draynor (F2P)
        SPOTS.add(new WoodcuttingSpot("Draynor Willows", new Area(3081, 3227, 3091, 3238), TreeType.WILLOW, true, null, false));
        
        // Maple - Seers (Members)
        SPOTS.add(new WoodcuttingSpot("Seers Maples", new Area(2717, 3498, 2735, 3503), TreeType.MAPLE, true, null, true));
        
        // Yew - Edgeville / Varrock Palace (F2P)
        SPOTS.add(new WoodcuttingSpot("Edgeville Yews", new Area(3085, 3468, 3089, 3482), TreeType.YEW, true, null, false));
        SPOTS.add(new WoodcuttingSpot("Varrock Palace Yews", new Area(3203, 3500, 3223, 3506), TreeType.YEW, true, null, false));
        
        // Magic - Seers (Members)
        SPOTS.add(new WoodcuttingSpot("Seers Magics", new Area(2690, 3422, 2697, 3427), TreeType.MAGIC, true, null, true));
    }
    
    public static WoodcuttingSpot getBestSpot(int level) {
        // Check membership status from World
        World currentWorld = Worlds.getCurrent();
        boolean isMembersWorld = (currentWorld != null && currentWorld.isMembers()) || Client.isMembers();

        // Simple logic: Highest tree we can chop that is defined
        // Reverse iterate to find highest level matching
        TreeType targetType = TreeType.NORMAL;
        
        if (level >= 75 && isMembersWorld) targetType = TreeType.MAGIC;
        else if (level >= 60) targetType = TreeType.YEW;
        else if (level >= 45 && isMembersWorld) targetType = TreeType.MAPLE;
        else if (level >= 30) targetType = TreeType.WILLOW;
        else if (level >= 15) targetType = TreeType.OAK;
        
        final TreeType finalTarget = targetType;
        
        return SPOTS.stream()
                .filter(s -> s.getTreeType() == finalTarget)
                .filter(s -> !s.isMembers() || isMembersWorld) // Only allow members spots if in members world
                .findFirst()
                .orElse(SPOTS.get(0));
    }
}
