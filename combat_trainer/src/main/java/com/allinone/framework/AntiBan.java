package com.allinone.framework;

import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.BooleanSupplier;

public class AntiBan {

    private final Random random = new Random();
    private long lastAntiBanTime = System.currentTimeMillis();
    private final long sessionStartTime = System.currentTimeMillis();

    // Intervals
    private int minInterval = 45000;  // 45s
    private int maxInterval = 300000; // 5 min
    private int nextInterval = 60000;

    // Fatigue simulation: tracks how long we've been running
    // After 30+ minutes, delays increase gradually
    private static final long FATIGUE_ONSET_MS = 30 * 60 * 1000;       // 30 min
    private static final double MAX_FATIGUE_MULTIPLIER = 1.8;          // Up to 80% slower at peak fatigue
    private static final long FATIGUE_PEAK_MS = 3 * 60 * 60 * 1000;    // 3 hours

    // Micro-break: occasionally pause longer (simulates looking away / distraction)
    private long lastMicroBreak = System.currentTimeMillis();
    private int microBreakInterval = 600000; // ~10 min between micro-breaks

    public AntiBan() {
        updateNextInterval();
        microBreakInterval = 300000 + random.nextInt(600000); // 5-15 min
    }

    /**
     * Call this inside your main loop or nodes.
     * Only executes if enough time has passed since last action.
     */
    public void performTimedAntiBan() {
        // Micro-break check: occasionally do a longer pause (2-6s) to simulate distraction
        if (System.currentTimeMillis() - lastMicroBreak > microBreakInterval) {
            int breakDuration = 2000 + random.nextInt(4000);
            Logger.log("AntiBan: Micro-break (" + breakDuration + "ms)");
            Sleep.sleep(breakDuration);
            lastMicroBreak = System.currentTimeMillis();
            microBreakInterval = 300000 + random.nextInt(600000);
        }

        if (System.currentTimeMillis() - lastAntiBanTime > nextInterval) {
            performRandomAction();
            lastAntiBanTime = System.currentTimeMillis();
            updateNextInterval();
        }
    }

    /**
     * Gaussian distributed sleep with fatigue modifier.
     */
    public void sleep(int mean, int stdDev) {
        double fatigue = getFatigueMultiplier();
        int adjustedMean = (int) (mean * fatigue);
        int adjustedDev = (int) (stdDev * fatigue);
        int sleepTime = (int) (random.nextGaussian() * adjustedDev + adjustedMean);
        if (sleepTime < 50) sleepTime = 50;
        Sleep.sleep(sleepTime);
    }

    /**
     * Static version for utility classes (no fatigue applied).
     */
    public static void sleepStatic(int mean, int stdDev) {
        int sleepTime = (int) (new Random().nextGaussian() * stdDev + mean);
        if (sleepTime < 50) sleepTime = 50;
        Sleep.sleep(sleepTime);
    }

    /**
     * Waits for a condition with periodic anti-ban checks.
     * Only performs non-disruptive actions (camera nudge) while waiting,
     * to avoid opening tabs during active skilling.
     */
    public void sleepUntil(BooleanSupplier condition, int timeout) {
        long start = System.currentTimeMillis();
        while (!condition.getAsBoolean() && System.currentTimeMillis() - start < timeout) {
            // Only do non-disruptive actions while waiting for a condition
            if (System.currentTimeMillis() - lastAntiBanTime > nextInterval) {
                nudgeCamera();
                lastAntiBanTime = System.currentTimeMillis();
                updateNextInterval();
            }
            Sleep.sleep(100);
        }
        // Human reaction delay after condition met
        sleep(400, 150);
    }

    /**
     * Rotates camera toward a target entity with human-like imprecision.
     * Call this before interacting with off-screen objects.
     */
    public void rotateCameraToEntity(Entity target) {
        if (target == null || target.isOnScreen()) return;

        // Use DreamBot's built-in rotateToEntity for reliable camera movement
        Camera.rotateToEntity(target);
        sleep(300, 100);
    }

    /**
     * Returns a fatigue multiplier (1.0 = fresh, up to MAX_FATIGUE_MULTIPLIER at peak).
     */
    private double getFatigueMultiplier() {
        long elapsed = System.currentTimeMillis() - sessionStartTime;
        if (elapsed < FATIGUE_ONSET_MS) return 1.0;

        double fatigueProgress = Math.min(1.0,
            (double) (elapsed - FATIGUE_ONSET_MS) / (FATIGUE_PEAK_MS - FATIGUE_ONSET_MS));

        return 1.0 + (MAX_FATIGUE_MULTIPLIER - 1.0) * fatigueProgress;
    }

    private void updateNextInterval() {
        nextInterval = random.nextInt(maxInterval - minInterval) + minInterval;
    }

    private void performRandomAction() {
        int roll = random.nextInt(100);
        Logger.log("AntiBan: Performing random action (Roll: " + roll + ")");

        if (roll < 25) {
            nudgeCamera();
        } else if (roll < 40) {
            checkStats();
        } else if (roll < 55) {
            checkRandomTab();
        } else if (roll < 70) {
            hoverInventoryItem();
        } else if (roll < 85) {
            examineNearbyEntity();
        } else {
            idlePause();
        }
    }

    /**
     * Incremental camera movement - small adjustments from current position.
     * Real players don't spin the camera to completely random positions.
     */
    private void nudgeCamera() {
        int currentYaw = Camera.getYaw();
        int currentPitch = Camera.getPitch();

        // Small yaw adjustment: 50-400 degrees in either direction
        int yawDelta = (50 + random.nextInt(350)) * (random.nextBoolean() ? 1 : -1);
        int newYaw = (currentYaw + yawDelta + 2048) % 2048;

        // Small pitch adjustment: +-80 from current, clamped to valid range
        int pitchDelta = -80 + random.nextInt(161);
        int newPitch = Math.max(128, Math.min(383, currentPitch + pitchDelta));

        Logger.log("AntiBan: Nudging camera (yaw " + currentYaw + " -> " + newYaw + ")");
        Camera.rotateTo(newYaw, newPitch);
        sleep(400, 150);
    }

    private void checkStats() {
        Logger.log("AntiBan: Checking Skills");
        if (Tabs.open(Tab.SKILLS)) {
            sleep(500, 100);
            Skills.hoverSkill(Skill.values()[random.nextInt(Skill.values().length)]);
            sleep(1000, 300);
            // Return to inventory like a real player would
            Tabs.open(Tab.INVENTORY);
            sleep(300, 100);
        }
    }

    private void checkRandomTab() {
        List<Tab> distinctTabs = Arrays.asList(
            Tab.INVENTORY, Tab.EQUIPMENT, Tab.MAGIC, Tab.PRAYER, Tab.FRIENDS
        );
        Tab t = distinctTabs.get(random.nextInt(distinctTabs.size()));
        Logger.log("AntiBan: Checking tab " + t.name());
        Tabs.open(t);
        sleep(600, 200);
        // Return to inventory
        if (t != Tab.INVENTORY) {
            sleep(400, 150);
            Tabs.open(Tab.INVENTORY);
            sleep(200, 80);
        }
    }

    /**
     * Hover over a random inventory item without clicking.
     * Common idle behavior for real players.
     */
    private void hoverInventoryItem() {
        if (Inventory.isEmpty()) {
            nudgeCamera();
            return;
        }

        if (!Tabs.isOpen(Tab.INVENTORY)) {
            Tabs.open(Tab.INVENTORY);
            sleep(300, 100);
        }

        // Just hover an item - real players glance at their inventory
        List<org.dreambot.api.wrappers.items.Item> items = new ArrayList<>();
        for (org.dreambot.api.wrappers.items.Item item : Inventory.all()) {
            if (item != null) items.add(item);
        }
        if (!items.isEmpty()) {
            int index = random.nextInt(items.size());
            Logger.log("AntiBan: Hovering inventory item");
            items.get(index).interact("Examine");
            sleep(400, 150);
        }
    }

    /**
     * Examine a nearby visible entity. Only targets entities that are on screen.
     */
    private void examineNearbyEntity() {
        List<Entity> candidates = new ArrayList<>();

        // Only add entities that are currently visible on screen
        candidates.addAll(NPCs.all(n ->
            n != null && n.isOnScreen() && n.distance(Players.getLocal()) < 7
        ));
        candidates.addAll(GameObjects.all(o ->
            o != null && o.isOnScreen() && o.distance(Players.getLocal()) < 7
            && o.getName() != null && !o.getName().equals("null")
        ));
        candidates.addAll(Players.all(p ->
            p != null && !p.equals(Players.getLocal())
            && p.isOnScreen() && p.distance(Players.getLocal()) < 7
        ));

        if (candidates.isEmpty()) {
            Logger.log("AntiBan: No visible entities to examine");
            nudgeCamera();
            return;
        }

        Entity target = candidates.get(random.nextInt(candidates.size()));
        if (target != null) {
            Logger.log("AntiBan: Examining " + target.getName());
            target.interact("Examine");
            sleep(500, 200);
        }
    }

    /**
     * Short idle pause - simulates momentary distraction.
     */
    private void idlePause() {
        int pause = 1500 + random.nextInt(2500);
        Logger.log("AntiBan: Idle pause (" + pause + "ms)");
        Sleep.sleep(pause);
    }
}
