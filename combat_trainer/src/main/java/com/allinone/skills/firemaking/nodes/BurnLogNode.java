package com.allinone.skills.firemaking.nodes;

import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.framework.AntiBan;
import com.allinone.framework.Blackboard;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.input.Keyboard;

public class BurnLogNode extends LeafNode {

    private final Blackboard blackboard;

    public BurnLogNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        if (!Inventory.contains("Tinderbox")) {
            return Status.FAILURE; // Can't burn
        }
        
        // Robust Log Finding
        Item log = Inventory.get(i -> i != null && i.getName() != null && i.getName().toLowerCase().contains("logs"));
        
        if (log == null) {
             return Status.FAILURE;
        }

        // Check if we strictly finished burning (Chat message "The fire has burned out")
        // No easy API for chat listener here unless we implement MessageListener in main script
        // But we can check animation. If we are NOT animating, we are idle.
        
        // Check if we are already adding to a fire
        if (Players.getLocal().isAnimating()) {
            blackboard.setCurrentStatus("Burning logs (Bonfire)");
            return Status.RUNNING;
        }
        
        // --- NEW LOGIC ---
        // 1. Look for "Forester's Campfire"
        GameObject forestersCampfire = GameObjects.closest(g -> g != null && g.getName().equals("Forester's campfire") && g.distance(Players.getLocal()) < 8);
        
        if (forestersCampfire != null) {
             log("Found Forester's Campfire. Tending to it.");
             blackboard.setCurrentStatus("Tending Forester's Campfire");
             
             if (forestersCampfire.interact("Tend-to") || log.useOn(forestersCampfire)) {
                 Sleep.sleepUntil(() -> Players.getLocal().isAnimating() || Players.getLocal().isMoving(), 3000);
                 if (handleMakeAll()) {
                     Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 3000);
                 }
                 return Status.RUNNING;
             }
        }

        // 2. Look for existing normal fire (Bonfire Mode fallback)
        GameObject nearbyFire = GameObjects.closest(g -> g != null && g.getName().equals("Fire") && g.distance(Players.getLocal()) < 6);
        
        if (nearbyFire != null) {
            log("Adding logs to existing fire: " + nearbyFire.getTile());
            blackboard.setCurrentStatus("Using logs on bonfire");
            if (log.useOn(nearbyFire)) {
                 // Wait for interface OR animation
                 // 270 is standard production interface
                 Sleep.sleepUntil(() -> Players.getLocal().isAnimating() || (Widgets.getWidget(270) != null && Widgets.getWidget(270).isVisible()), 3000);
                 
                 // Handle specific "How many logs?" interface if it appears
                 if (handleMakeAll()) {
                     Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 3000);
                 }

                 AntiBan.sleepStatic(600, 200);
                 return Status.RUNNING;
            }
        }
        
        // 3. If no fire nearby, we must light one
        log("No fire nearby. Lighting new fire.");
        blackboard.setCurrentStatus("Lighting first fire");
        
        // Ensure tile clear check just for the initial fire
        Tile myTile = Players.getLocal().getTile();
        if (isTileBlocked(myTile)) {
            // Find nearby clear tile
             log("Blocked tile. Finding clear spot.");
             Tile[] nearby = {
                 myTile.translate(1, 0), myTile.translate(-1, 0),
                 myTile.translate(0, 1), myTile.translate(0, -1)
             };
             for (Tile t : nearby) {
                 if (Walking.canWalk(t) && !isTileBlocked(t)) {
                     Walking.walkExact(t);
                     Sleep.sleepUntil(() -> !Players.getLocal().isMoving(), 2500);
                     return Status.RUNNING;
                 }
             }
        }

        if (log.useOn("Tinderbox")) {
             // Wait for init
             Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 3000);
             Sleep.sleepUntil(() -> !Players.getLocal().isAnimating() || GameObjects.closest("Forester's campfire") != null, 8000); 
             // Once lit, next loop will catch "nearbyFire" or "Forester's campfire" and start adding logs
             return Status.RUNNING;
        }

        return Status.FAILURE;
    }

    private boolean handleMakeAll() {
        // Standard Make-All Interface (ID 270)
        if (Widgets.getWidget(270) != null && Widgets.getWidget(270).isVisible()) {
            log("Make-All Interface detected. Pressing Space.");
            Keyboard.type(" ");
            return true;
        }
        
        // Chatbox "How many logs?" Interface (Usually matches text)
        // Check for general "Burn" action in chatbox widget area or similar
        // Often simpler just to press Space if the dialog is open and focused
        if (Widgets.getWidget(162) != null && Widgets.getWidget(162).isVisible()) { // 162 is often chatbox layer
             // This is a bit speculative without ID, but Space is safe for most default options
             // Keyboard.type(" "); 
        }
        
        return false;
    }

    private boolean isTileBlocked(Tile t) {
        // Use getObjectsOnTile for reliable checking
        GameObject[] objs = GameObjects.getObjectsOnTile(t);
        for (GameObject g : objs) {
            if (g != null && (g.getName().equals("Fire") || g.getName().equals("Daisy") || g.getName().contains("Femur"))) {
                return true;
            }
        }
        return false;
    }
}
