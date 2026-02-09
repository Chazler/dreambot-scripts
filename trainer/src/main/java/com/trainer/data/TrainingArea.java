package com.trainer.data;

import com.trainer.framework.Goal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;

public class TrainingArea {
    
    // Woodcutting
    public static final Area LUMBRIDGE_TREES = new Area(3190, 3240, 3205, 3220);
    public static final Area LUMBRIDGE_OAKS = new Area(3100, 3240, 3150, 3220);
    public static final Area DRAYNOR_WILLOWS = new Area(3082, 3238, 3093, 3229);
    
    // Mining
    public static final Area LUMBRIDGE_SWAMP_MINE = new Area(3221, 3149, 3230, 3143);
    
    // Fishing
    public static final Area DRAYNOR_FISHING = new Area(3084, 3232, 3092, 3224);
    
    // Combat
    public static final Area LUMBRIDGE_GOBLINS = new Area(3239, 3254, 3267, 3226);

    public static Area getArea(Goal goal) {
        if (goal == null) return null;

        int level = Skills.getRealLevel(goal.getSkill());

        switch (goal.getSkill()) {
            case WOODCUTTING:
                if (level >= 30) return DRAYNOR_WILLOWS;
                if (level >= 15) return LUMBRIDGE_OAKS;
                return LUMBRIDGE_TREES;
                
            case MINING:
                return LUMBRIDGE_SWAMP_MINE;
                
            case FISHING:
                return DRAYNOR_FISHING;
                
            case ATTACK:
            case STRENGTH:
            case DEFENCE:
            case RANGED:
            case MAGIC:
                return LUMBRIDGE_GOBLINS;
                
            default:
                return null;
        }
    }
}
