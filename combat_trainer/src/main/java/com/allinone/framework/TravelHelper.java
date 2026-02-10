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
        if (area == null) {
            Logger.log("TravelHelper: Area is null");
            return;
        }
        if (area.contains(Players.getLocal())) {
            Logger.log("TravelHelper: Already in area");
            return;
        }
        
        // Human-like: Don't spam click if moving
        if (Players.getLocal().isMoving()) {
             // Logger.log("TravelHelper: Player is moving (Area)"); // Verbose
             return;
        }
        
        AntiBan.sleepStatic(600, 250);
        Tile randomTile = area.getRandomTile();
        Logger.log("TravelHelper: Area travel -> Random Tile: " + randomTile);
        travelTo(randomTile);
    }

    /**
     * Intelligent travel using Teleports if distance is great, otherwise Walking.
     */
    public static void travelTo(Tile target) {
        if (target == null){
            Logger.log("TravelHelper: Target is null");
            return;
        }
        
        // 1. Human-like: Check moving status
        Tile dest = Walking.getDestination();
        if (Players.getLocal().isMoving() && dest != null && dest.distance(target) < 5) {
            // Logger.log("TravelHelper: Already moving near target");
            return;
        }

        Tile myPos = Players.getLocal().getTile();
        double distanceToTarget = myPos.distance(target);
        Logger.log("TravelHelper: Dist to target: " + (int)distanceToTarget + " Tile: " + target);
        
        // 2. Human-like: Toggle Run (Enable if > 20% and not enabled)
        // Add random condition so we don't toggle exactly at 20 every time
        if (Walking.getRunEnergy() > 20 && !Walking.isRunEnabled() && Math.random() > 0.5) {
            Walking.toggleRun();
            AntiBan.sleepStatic(400, 100);
        }

        // If we are close, just walk
        if (distanceToTarget < 20) {
            Logger.log("TravelHelper: Short range walk");
            if (Walking.walk(target)) {
                AntiBan.sleepStatic(900, 150); // Wait for reaction
            } else {
                Logger.log("TravelHelper: Short range walk failed");
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
        Logger.log("TravelHelper: Long range walk/WebWalk");
        if (Walking.walk(target)) {
            AntiBan.sleepStatic(900, 150);
        } else {
            Logger.log("TravelHelper: WebWalk failed");
        }
    }
}
