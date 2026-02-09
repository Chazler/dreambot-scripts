package com.allinone.framework;

import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Spell;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;

public class TravelHelper {

    private enum TeleportLocation {
        VARROCK(new Tile(3210, 3424, 0), Normal.VARROCK_TELEPORT),
        LUMBRIDGE(new Tile(3222, 3218, 0), Normal.LUMBRIDGE_TELEPORT),
        FALADOR(new Tile(2964, 3378, 0), Normal.FALADOR_TELEPORT),
        CAMELOT(new Tile(2757, 3477, 0), Normal.CAMELOT_TELEPORT),
        ARDOUGNE(new Tile(2662, 3305, 0), Normal.ARDOUGNE_TELEPORT);

        private final Tile tile;
        private final Spell spell;

        TeleportLocation(Tile tile, Spell spell) {
            this.tile = tile;
            this.spell = spell;
        }

        public Tile getTile() { return tile; }
        public Spell getSpell() { return spell; }
    }

    /**
     * Travels to the center of the area.
     */
    public static void travelTo(Area area) {
        if (area == null) return;
        if (area.contains(Players.getLocal())) {
            return;
        }
        
        // Human-like: Don't spam click if moving
        if (Players.getLocal().isMoving()) {
             return;
        }
        
        Tile randomTile = area.getRandomTile();
        travelTo(randomTile);
    }

    /**
     * Intelligent travel using Teleports if distance is great, otherwise Walking.
     */
    public static void travelTo(Tile target) {
        if (target == null) return;
        
        // 1. Human-like: Check moving status
        Tile dest = Walking.getDestination();
        if (Players.getLocal().isMoving() && dest != null && dest.distance(target) < 5) {
             return;
        }

        Tile myPos = Players.getLocal().getTile();
        double distanceToTarget = myPos.distance(target);
        
        // 2. Human-like: Toggle Run (Enable if > 20% and not enabled)
        // Add random condition so we don't toggle exactly at 20 every time
        if (Walking.getRunEnergy() > 20 && !Walking.isRunEnabled() && Math.random() > 0.5) {
             Walking.toggleRun();
             AntiBan.sleepStatic(400, 100);
        }

        // If we are close, just walk
        if (distanceToTarget < 20) {
            if (Walking.walk(target)) {
                AntiBan.sleepStatic(900, 150); // Wait for reaction
            }
            return;
        }

        // Check if we should teleport
        if (distanceToTarget > 100) { // Arbitrary threshold for teleporting
            TeleportLocation bestTeleport = null;
            double bestDist = distanceToTarget; // Start with current distance (walking)

            for (TeleportLocation loc : TeleportLocation.values()) {
                double distFromTeleport = loc.getTile().distance(target);
                
                // If this teleport puts us closer than we are now
                if (distFromTeleport < bestDist) {
                    // Check if we can cast it
                    if (Magic.canCast(loc.getSpell())) {
                        bestTeleport = loc;
                        bestDist = distFromTeleport;
                    }
                }
            }

            if (bestTeleport != null) {
                Logger.log("Teleporting to " + bestTeleport.name() + " to get closer to target.");
                if (Magic.castSpell(bestTeleport.getSpell())) {
                     Sleep.sleepUntil(() -> myPos.distance(Players.getLocal().getTile()) > 10, 5000);
                     AntiBan.sleepStatic(900, 150);
                }
                return; // Return and wait for next loop to handle walking from teleport spot
            }
        }

        // Default: Walk
        // If web walking is enabled, this handles pathfinding
        if (Walking.walk(target)) {
            AntiBan.sleepStatic(900, 150);
        }
    }
}
