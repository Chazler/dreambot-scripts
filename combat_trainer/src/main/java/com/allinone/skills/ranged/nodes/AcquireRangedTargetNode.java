package com.allinone.skills.ranged.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.skills.ranged.data.RangedTrainingLocation;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.wrappers.interactive.NPC;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class AcquireRangedTargetNode extends LeafNode {

    private final Blackboard blackboard;

    public AcquireRangedTargetNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        if (Players.getLocal().isInCombat()) {
            return Status.SUCCESS;
        }

        RangedTrainingLocation loc = blackboard.getCurrentRangedLocation();
        if (loc == null) return Status.FAILURE;

        blackboard.setCurrentStatus("Acquiring target...");

        String[] npcNames = loc.getNpcNames();

        // Find NPCs matching the location's defined list
        List<NPC> candidates = NPCs.all(n ->
            n != null
            && Arrays.asList(npcNames).contains(n.getName())
            && n.hasAction("Attack")
            && !n.isInCombat()
            && loc.getArea().contains(n)
        );

        if (candidates.isEmpty()) {
            // Fallback: accept NPCs in combat too
            candidates = NPCs.all(n ->
                n != null
                && Arrays.asList(npcNames).contains(n.getName())
                && n.hasAction("Attack")
                && n.getHealthPercent() > 0
                && loc.getArea().contains(n)
            );
        }

        if (candidates.isEmpty()) {
            log("No targets found!");
            return Status.FAILURE;
        }

        // Score: prefer closest non-combat NPC
        NPC best = candidates.stream()
                .min(Comparator.comparingDouble(n -> n.distance(Players.getLocal())))
                .orElse(null);

        if (best != null) {
            blackboard.setCurrentTarget(best);
            return Status.SUCCESS;
        }

        return Status.FAILURE;
    }
}
