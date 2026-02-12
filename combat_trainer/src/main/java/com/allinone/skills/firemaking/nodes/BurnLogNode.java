package com.allinone.skills.firemaking.nodes;

import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.framework.Blackboard;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.input.Camera;
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
    private long lastActivityTime = System.currentTimeMillis();
    private int stuckCount = 0;

    public BurnLogNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        if (!Inventory.contains("Tinderbox")) {
            return Status.FAILURE;
        }

        Item log = Inventory.get(i -> i != null && i.getName() != null && i.getName().toLowerCase().contains("logs"));
        if (log == null) {
            return Status.FAILURE;
        }

        // Already burning - track activity
        if (Players.getLocal().isAnimating()) {
            lastActivityTime = System.currentTimeMillis();
            stuckCount = 0;
            blackboard.setCurrentStatus("Burning logs (Bonfire)");
            return Status.RUNNING;
        }

        // Check for Make-All interface that appeared while we weren't animating
        if (handleMakeAll()) {
            Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 3000);
            lastActivityTime = System.currentTimeMillis();
            return Status.RUNNING;
        }

        // Idle timeout: if nothing has happened for 20s, force relocate
        if (System.currentTimeMillis() - lastActivityTime > 20000) {
            stuckCount++;
            log("Idle too long during firemaking (attempt " + stuckCount + ") - resetting");
            lastActivityTime = System.currentTimeMillis();
            if (stuckCount >= 3) {
                log("Stuck too many times - forcing skill stop");
                blackboard.setForceStopSkill(true);
                stuckCount = 0;
            }
            blackboard.setForceFiremakingRelocate(true);
            return Status.FAILURE;
        }

        // 1. Look for "Forester's Campfire"
        GameObject forestersCampfire = GameObjects.closest(g ->
            g != null && g.getName() != null && g.getName().equals("Forester's Campfire") && g.distance(Players.getLocal()) < 15
        );

        if (forestersCampfire != null) {
            log("Found Forester's Campfire. Tending to it.");
            blackboard.setCurrentStatus("Tending Forester's Campfire");

            // Rotate camera if off-screen
            if (!forestersCampfire.isOnScreen()) {
                Camera.rotateToEntity(forestersCampfire);
                blackboard.getAntiBan().sleep(400, 150);
            }

            if (forestersCampfire.interact("Tend-to") || log.useOn(forestersCampfire)) {
                blackboard.getAntiBan().sleep(600, 200);
                Sleep.sleepUntil(() -> Players.getLocal().isAnimating() || Players.getLocal().isMoving()
                    || isWidgetVisible(270), 4000);
                if (handleMakeAll()) {
                    Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 3000);
                }
                lastActivityTime = System.currentTimeMillis();
                return Status.RUNNING;
            }
        }

        // 2. Look for existing normal fire (Bonfire Mode fallback)
        GameObject nearbyFire = GameObjects.closest(g ->
            g != null && g.getName().equals("Fire") && g.distance(Players.getLocal()) < 6
        );

        if (nearbyFire != null) {
            log("Adding logs to existing fire: " + nearbyFire.getTile());
            blackboard.setCurrentStatus("Using logs on bonfire");

            if (!nearbyFire.isOnScreen()) {
                Camera.rotateToEntity(nearbyFire);
                blackboard.getAntiBan().sleep(400, 150);
            }

            if (log.useOn(nearbyFire)) {
                blackboard.getAntiBan().sleep(600, 200);
                Sleep.sleepUntil(() -> Players.getLocal().isAnimating() || isWidgetVisible(270), 4000);
                if (handleMakeAll()) {
                    Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 3000);
                }
                lastActivityTime = System.currentTimeMillis();
                return Status.RUNNING;
            }
        }

        // 3. If no fire nearby, we must light one
        log("No fire nearby. Lighting new fire.");
        blackboard.setCurrentStatus("Lighting first fire");

        Tile myTile = Players.getLocal().getTile();
        if (isTileBlocked(myTile)) {
            log("Blocked tile. Finding clear spot.");
            Tile[] nearby = {
                myTile.translate(1, 0), myTile.translate(-1, 0),
                myTile.translate(0, 1), myTile.translate(0, -1),
                myTile.translate(1, 1), myTile.translate(-1, -1)
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
            blackboard.getAntiBan().sleep(500, 200);
            Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 4000);
            Sleep.sleepUntil(() -> !Players.getLocal().isAnimating()
                || GameObjects.closest("Forester's Campfire") != null, 10000);
            lastActivityTime = System.currentTimeMillis();
            return Status.RUNNING;
        }

        return Status.FAILURE;
    }

    private boolean handleMakeAll() {
        if (isWidgetVisible(270)) {
            log("Make-All Interface detected. Pressing Space.");
            blackboard.getAntiBan().sleep(300, 100);
            Keyboard.type(" ");
            return true;
        }
        return false;
    }

    private boolean isWidgetVisible(int widgetId) {
        return Widgets.getWidget(widgetId) != null && Widgets.getWidget(widgetId).isVisible();
    }

    private boolean isTileBlocked(Tile t) {
        GameObject[] objs = GameObjects.getObjectsOnTile(t);
        if (objs == null) return false;
        for (GameObject g : objs) {
            if (g != null && g.getName() != null
                    && (g.getName().equals("Fire") || g.getName().equals("Daisy") || g.getName().contains("Femur") || g.getName().equals("Forester's Campfire"))) {
                return true;
            }
        }
        return false;
    }
}
