package com.allinone.skills.mining.nodes;

import com.allinone.framework.AntiBan;
import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.skills.mining.data.MiningSpot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.utilities.Logger;

import java.util.Comparator;

public class FindRockNode extends LeafNode {

    private final Blackboard blackboard;

    public FindRockNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        MiningSpot spot = blackboard.getCurrentMiningSpot();
        if (spot == null) return Status.FAILURE;
        
        // Find rock within the defined area
        // Ore rocks usually have name "Rocks" or "Ore vein". The color usually determines type.
        // However, RockType enum has 'ObjectName' field ("Copper rocks").
        // Dreambot objects for rocks often have names like "Copper rocks" or just "Rocks" with modified colors.
        // But usually, filtering by name "Copper rocks" works if using `GameObjects`.
        // Actually, many rocks are just named "Rocks" and you need to filter by ID or color.
        // BUT, modern Dreambot often resolves "Copper rocks" if the definition matches.
        
        // Let's rely on filter: ID or Name. 
        // Best approach: Filter by name containing the Ore Name? 
        // e.g. "Copper rocks", "Tin rocks".
        
        GameObject rock = GameObjects.closest(r -> {
            if (r == null || !spot.getArea().contains(r)) return false;
            
            // Competition Filter
            if (isOtherPlayerMining(r)) return false;
            
            // Name Filter
            // If spot is TIN, we allow Tin OR Copper? 
            // Or only if we are in "Varrock East".
            // Let's implement dynamic type matching for low levels.
            String targetName = spot.getRockType().getObjectName();
            
            boolean nameMatch = r.getName().equals(targetName);
            
            // Allow mixing Copper/Tin if one is requested
            if (!nameMatch && (spot.getRockType() == com.allinone.skills.mining.data.RockType.TIN || spot.getRockType() == com.allinone.skills.mining.data.RockType.COPPER)) {
                 String n = r.getName();
                 if (n.equals("Copper rocks") || n.equals("Tin rocks")) {
                     nameMatch = true;
                 }
            }

            return nameMatch && r.hasAction("Mine");
        });
        
        
        blackboard.setCurrentStatus("Searching for " + spot.getRockType().getObjectName() + "...");
        return Status.FAILURE;
    }

    private boolean isOtherPlayerMining(GameObject rock) {
        if (rock == null) return false;
        return !Players.all(p -> 
            p != null && 
            !p.equals(Players.getLocal()) && 
            p.distance(rock) <= 1 && 
            p.isAnimating() 
        ).isEmpty();
    }
}
