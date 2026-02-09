package com.allinone.skills.firemaking.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import com.allinone.framework.TravelHelper;

import com.allinone.skills.firemaking.data.StaticBurnAreas;

import java.util.HashSet;
import java.util.Set;
import org.dreambot.api.wrappers.interactive.GameObject;

public class TravelToBurnAreaNode extends LeafNode {

    private final Blackboard blackboard;

    public TravelToBurnAreaNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        StaticBurnAreas.BurnArea target = StaticBurnAreas.getNearest();
        if (target == null) 
        {
            log("Couldn't travel to burn area.");
            blackboard.setCurrentStatus("No burn areas found.");
            return Status.FAILURE;
        }

        // If we are starting a fresh run (Full Inventory), ensure we pick a clear line
        if (Inventory.isFull() || blackboard.isForceFiremakingRelocate()) {
             if (blackboard.isForceFiremakingRelocate()) {
                 log("Relocating to fresh line due to blockage.");
                 blackboard.setForceFiremakingRelocate(false);
             }

             Tile startTile = getClearStartTile(target.getArea());
             if (startTile != null) {
                 if (Players.getLocal().getTile().equals(startTile)) {
                     return Status.SUCCESS; // Ready to burn
                 }
                 
                 log("Moving to clear fire line start: " + startTile);
                 blackboard.setCurrentStatus("Aligning to clear fire line");
                 
                 // Use WalkExact for precision at the end
                 if (startTile.distance(Players.getLocal()) < 5) {
                    log("Walk Exact");
                    Walking.walkExact(startTile);
                 } else {
                    log("travel helper");
                    TravelHelper.travelTo(startTile);
                 }
                 return Status.RUNNING;
             } else {
                 log("No clear fire lines found! Using default area.");
             }
        }

        if (target.getArea().contains(Players.getLocal())) {
             return Status.SUCCESS;
        }

        // Mid-burn check: If we are not in the start area, but we have logs and are reasonably close
        // (e.g. within 40 tiles - roughly a full inventory line), assume we are fine.
        if (target.getArea().distance(Players.getLocal().getTile()) < 40) {
             return Status.SUCCESS;
        }

        if (Players.getLocal().isMoving()) {
            return Status.RUNNING;
        }
        
        log("Travelling to burn area");
        blackboard.setCurrentStatus("Walking to Burn Area: " + target.getName());
        TravelHelper.travelTo(target.getArea());
        
        return Status.RUNNING;
    }

    private Tile getClearStartTile(Area area) {
        Tile[] candidates = area.getTiles();
        
        // Human-like optimization: Cache all fires once instead of querying API 1000 times
        Set<Tile> fireTiles = new HashSet<>();
        for (GameObject g : GameObjects.all("Fire")) {
            if (g != null) {
                fireTiles.add(g.getTile());
            }
        }
        
        Tile best = null;
        double minDst = Double.MAX_VALUE;

        for (Tile t : candidates) {
            boolean blocked = false;
            // Check 20 tiles to the West
            for (int i = 0; i < 20; i++) {
                // Check current tile (i=0) and next 19 west
                Tile check = new Tile(t.getX() - i, t.getY(), t.getZ());
                
                if (fireTiles.contains(check)) {
                    blocked = true;
                    break;
                }
                
                // Optional: Check collision map if desired, but "Fire" check is the main issue
            }

            if (!blocked) {
                double dst = t.distance(Players.getLocal());
                if (dst < minDst) {
                    minDst = dst;
                    best = t;
                }
            }
        }
        
        return best;
    }
}

