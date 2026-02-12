package com.allinone.skills.magic.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.skills.magic.data.MagicSpell;
import com.allinone.skills.magic.data.MagicTrainingLocation;
import com.allinone.skills.magic.strategies.MagicManager;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.wrappers.interactive.NPC;

import java.util.Arrays;

public class FindMagicTargetNode extends LeafNode {

    private final Blackboard blackboard;

    public FindMagicTargetNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        MagicTrainingLocation loc = MagicManager.getBestLocation();
        if (loc == null) return Status.FAILURE;

        String[] npcNames = loc.getNpcNames();

        NPC target = NPCs.closest(n ->
            n != null
            && Arrays.asList(npcNames).contains(n.getName())
            && !n.isInCombat()
            && n.getHealthPercent() > 0
            && loc.getArea().contains(n)
        );

        if (target != null) {
            blackboard.setCurrentTarget(target);
            blackboard.setCurrentStatus("Found target: " + target.getName());
            return Status.SUCCESS;
        }

        // Fallback: accept any matching NPC even if in combat
        target = NPCs.closest(n ->
            n != null
            && Arrays.asList(npcNames).contains(n.getName())
            && n.getHealthPercent() > 0
            && loc.getArea().contains(n)
        );

        if (target != null) {
            blackboard.setCurrentTarget(target);
            return Status.SUCCESS;
        }

        blackboard.setCurrentStatus("Searching for target...");
        return Status.FAILURE;
    }
}
