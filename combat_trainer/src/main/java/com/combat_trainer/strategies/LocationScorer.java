package com.combat_trainer.strategies;

import com.combat_trainer.data.LocationDef;
import com.combat_trainer.data.StaticLocationData;
import org.dreambot.api.methods.interactive.Players;
import java.util.Comparator;

public class LocationScorer {

    public static LocationDef getBestLocation() {
        if (Players.getLocal() == null) return StaticLocationData.ALL_LOCATIONS.get(0);
        int combatLevel = Players.getLocal().getLevel();
        
        // Find locations where combat level is at least the recommended, but not TOO high (diminishing XP)
        // This is a heuristic function.
        
        return StaticLocationData.ALL_LOCATIONS.stream()
                .map(loc -> new ScoredLocation(loc, score(loc, combatLevel)))
                .max(Comparator.comparingDouble(ScoredLocation::getScore))
                .map(ScoredLocation::getLocation)
                .orElse(StaticLocationData.ALL_LOCATIONS.get(0)); // Fallback to first
    }
    
    private static double score(LocationDef loc, int combatLevel) {
        // Base score is expected XP
        double score = loc.getExpectedXpPerHour();
        
        // Penalty if we are too weak
        if (combatLevel < loc.getRecommendedCombatLevel()) {
            score *= 0.1; // Huge penalty
        }
        
        // Penalty if we are way too strong (waste of time)
        if (combatLevel > loc.getRecommendedCombatLevel() + 40) {
            score *= 0.5;
        }
        
        // Safety multiplier
        score *= (loc.getSafetyRating() / 10.0);
        
        return score;
    }
    
    private static class ScoredLocation {
        private final LocationDef location;
        private final double score;
        
        public ScoredLocation(LocationDef location, double score) {
            this.location = location;
            this.score = score;
        }
        
        public LocationDef getLocation() { return location; }
        public double getScore() { return score; }
    }
}
