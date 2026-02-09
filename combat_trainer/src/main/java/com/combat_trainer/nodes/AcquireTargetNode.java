package com.combat_trainer.nodes;

import com.combat_trainer.data.LocationDef;
import com.combat_trainer.framework.Blackboard;
import com.combat_trainer.framework.LeafNode;
import com.combat_trainer.framework.Status;
import com.combat_trainer.strategies.TargetScorer;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.wrappers.interactive.NPC;

import java.util.Comparator;
import java.util.List;

public class AcquireTargetNode extends LeafNode {
    private final Blackboard blackboard;

    public AcquireTargetNode(Blackboard blackboard) {
        super();
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        blackboard.setCurrentStatus("Acquiring Target...");
        // If we already have a valid target, we might skip this or re-evaluate
        // For a sequence, we usually want to ensure we have a FRESH target if we need one
        if (Players.getLocal().isInCombat()) {
            return Status.SUCCESS; // Already validly fighting
        }
        
        LocationDef loc = blackboard.getBestLocation();
        if (loc == null) return Status.FAILURE;
        
        // Find NPCs matching the location's defined list
        // Filter out NPCs that are already fighting someone else (unless they are fighting us, which is handled by isInCombat logic typically, 
        // strictly speaking if we are not in combat, we want a fresh target).
        List<NPC> candidates = NPCs.all(n -> 
            loc.getNpcNames().contains(n.getName()) && 
            n.hasAction("Attack") && 
            !n.isInCombat()
        );
        
        if (candidates.isEmpty()) {
            log("No targets found!");
            return Status.FAILURE;
        }
        
        // Score them
        NPC bestParam = candidates.stream()
                .max(Comparator.comparingDouble(n -> TargetScorer.scoreTarget(n, Players.getLocal())))
                .orElse(null);
                
        if (bestParam != null) {
            blackboard.setCurrentTarget(bestParam);
            return Status.SUCCESS;
        }
        
        return Status.FAILURE;
    }
}
