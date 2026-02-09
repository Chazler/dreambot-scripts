package com.allinone.skills.smithing.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.framework.TravelHelper;
import com.allinone.skills.smithing.data.SmithingItem;
import com.allinone.skills.smithing.data.SmithingType;
import com.allinone.skills.smithing.strategies.SmithingManager;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.methods.map.Area;

public class TravelToSmithingLocationNode extends LeafNode {

    private final Blackboard blackboard;

    public TravelToSmithingLocationNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        SmithingItem item = SmithingManager.getBestItem();
        String targetObjectName = (item.getType() == SmithingType.SMELTING) ? "Furnace" : "Anvil";
        
        GameObject nearest = GameObjects.closest(targetObjectName);
        
        if (nearest != null && nearest.distance(Players.getLocal()) < 10) {
            return Status.SUCCESS;
        }

        if (Players.getLocal().isMoving()) {
            return Status.RUNNING;
        }

        // Simplistic approach: Search broadly or go to known locations
        // For now, let's rely on finding standard GameObjects if within region, 
        // or hardcode a fallback if null (like Al Kharid Furnace or Varrock Anvil)
        
        if (nearest == null) {
            // Fallback for demo purposes - usually we'd have a Location system
            log("No " + targetObjectName + " found nearby. Searching...");
            // Real bot would pick a specific bank/furnace combo.
            // For now, let's assume we are in a bank and the furnace/anvil is nearby 
            // OR use WebWalking to find "Furnace"
            
            // DreamBot webwalker is generally good at 'Walk to X', but we need an Area or Tile usually.
            // Let's assume the user starts near one or we use a known Area.
            
            // Allow TravelHelper specific behavior??
            // Implementation detail: Use specific known locations if none visible?
            // Let's stick to simple "If visible walk, if not fail" for MVP, 
            // OR predefined areas.
            
            // Safe Default: Varrock West (Anvil), Edgeville (Furnace), Al Kharid (Furnace)
            if (item.getType() == SmithingType.SMELTING) {
               // Al Kharid Furnace Area
                log("Walking to Al Kharid Furnace (Default)");
                org.dreambot.api.methods.walking.impl.Walking.walk(new org.dreambot.api.methods.map.Tile(3275, 3186, 0));
            } else {
               // Varrock West Anvil Area
               log("Walking to Varrock Anvil (Default)");
               org.dreambot.api.methods.walking.impl.Walking.walk(new org.dreambot.api.methods.map.Tile(3186, 3425, 0));
            }
             return Status.RUNNING;
        }
        
        if (nearest != null) {
             blackboard.setCurrentStatus("Walking to " + targetObjectName);
             TravelHelper.travelTo(nearest.getTile().getArea(1));
             return Status.RUNNING;
        }

        return Status.FAILURE;
    }
}
