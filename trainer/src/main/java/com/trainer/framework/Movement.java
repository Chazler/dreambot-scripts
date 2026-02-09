package com.trainer.framework;

import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.Client;

public class Movement {
    
    /**
     * Blocks until the player is inside the target area.
     * Uses the web walker.
     */
    public static void walkTo(Area area) {
         if (area == null) return;
         
         while (!area.contains(Players.getLocal()) && Client.isLoggedIn()) {
             if (Walking.shouldWalk(6)) {
                 Walking.walk(area.getRandomTile());
             }
             // Small sleep to prevent CPU burn, but Washing.walk handles logic mostly.
             try { Thread.sleep(600); } catch (InterruptedException e) {}
         }
    }
}
