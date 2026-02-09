package com.combat_trainer.strategies;

import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;

import java.util.List;

public class TargetScorer {

    public static double scoreTarget(NPC npc, Player localPlayer) {
        double score = 100;

        // Distance factor (closer is better)
        double dist = npc.distance(localPlayer);
        score -= dist * 2; // -2 points per tile

        // Health factor (prefer full HP or damaged by us?)
        // Usually we want full HP targets if not already in combat
        if (npc.getHealthPercent() < 100 && !npc.isInteracting(localPlayer)) {
             score -= 50; // Don't steal kills
        }
        
        // Avoid crowded areas (simple check)
        if (Players.all().stream().anyMatch(p -> !p.equals(localPlayer) && p.distance(npc) < 2)) {
            score -= 30;
        }
        
        // Aggression bonus implementation would go here if we tracked aggression
        
        return score;
    }
}
