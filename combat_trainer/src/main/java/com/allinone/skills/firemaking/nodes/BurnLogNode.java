package com.allinone.skills.firemaking.nodes;

import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.methods.walking.impl.Walking;

public class BurnLogNode extends LeafNode {

    @Override
    public Status execute() {
        if (!Inventory.contains("Tinderbox")) {
            return Status.FAILURE; // Can't burn
        }
        
        // Ensure we are not standing on a fire
        Tile myTile = Players.getLocal().getTile();
        GameObject fire = GameObjects.closest(g -> g.getName().equals("Fire") && g.getTile().equals(myTile));
        if (fire != null) {
            // Move!
            // Walk 1 tile west or east
            Tile safe = myTile.translate(-1, 0);
            if (!Walking.canWalk(safe)) safe = myTile.translate(1, 0);
             if (!Walking.canWalk(safe)) safe = myTile.translate(0, 1);
            
            Walking.walkExact(safe);
            Sleep.sleepUntil(() -> !Players.getLocal().isMoving(), 2000);
            return Status.RUNNING;
        }

        Item log = Inventory.get(i -> i.getName().contains("logs"));
        if (log == null) return Status.FAILURE;
        
        if (Inventory.contains("Tinderbox") && log != null) {
            if (log.useOn("Tinderbox")) { // Or vice versa
                 // Wait for fire
                 Sleep.sleepUntil(() -> 
                     Players.getLocal().isAnimating(), 2000);
                 
                 Sleep.sleepUntil(() -> 
                     GameObjects.closest(g -> g.getName().equals("Fire") && g.getTile().equals(myTile)) != null, 
                     8000
                 );
                 return Status.RUNNING;
            }
        }

        return Status.FAILURE;
    }
}
