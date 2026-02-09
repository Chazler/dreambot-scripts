package com.allinone.framework;

import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Entity;
// Mouse removed until resolved, using Camera/Tabs only for now

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.awt.Point;

public class AntiBan {
    
    private final Random random = new Random();
    private long lastAntiBanTime = System.currentTimeMillis();
    
    // Configurable intervals - Increased to lower frequency
    private int minInterval = 45000; // 45s
    private int maxInterval = 300000; // 5 mins
    private int nextInterval = 60000; // Start with 60s

    public AntiBan() {
        updateNextInterval();
    }

    /**
     * Call this inside your main loop or nodes (e.g. while traveling or fighting).
     * It will only execute if enough time has passed.
     */
    public void performTimedAntiBan() {
        if (System.currentTimeMillis() - lastAntiBanTime > nextInterval) {
            performRandomAction();
            lastAntiBanTime = System.currentTimeMillis();
            updateNextInterval();
        }
    }

    /**
     * Gaussian distributed sleep.
     * @param mean The average sleep time in ms.
     * @param stdDev The standard deviation in ms.
     */
    public void sleep(int mean, int stdDev) {
        sleepStatic(mean, stdDev);
    }
    
    /**
     * Static version for utility classes.
     */
    public static void sleepStatic(int mean, int stdDev) {
        int sleepTime = (int) (new Random().nextGaussian() * stdDev + mean);
        if (sleepTime < 50) sleepTime = 50; 
        Sleep.sleep(sleepTime);
    }


    public void sleepUntil(BooleanSupplier condition, int timeout) {
        long start = System.currentTimeMillis();
        while (!condition.getAsBoolean() && System.currentTimeMillis() - start < timeout) {
            performTimedAntiBan(); // Check if we should do an anti-ban action while waiting
            Sleep.sleep(100); // Check condition every 100ms
        }
        // additional sleep after condition is met to mimic human reaction time
        sleep(500, 200);
    }
    
    private void updateNextInterval() {
        nextInterval = random.nextInt(maxInterval - minInterval) + minInterval;
    }

    private void performRandomAction() {
        int roll = random.nextInt(100);
        Logger.log("AntiBan: Performing random action (Roll: " + roll + ")");
        
        if (roll < 20) {
            moveCamera();
        } else if (roll < 40) {
            checkStats();
        } else if (roll < 70) {
            checkRandomTab();
        } else {
            // 30% chance to right click random entity
            rightClickRandomEntity();
        }
    }

    private void moveCamera() {
        int pitch = random.nextInt(383); // 0-383 is pitch range
        int yaw = random.nextInt(2048);
        Logger.log("AntiBan: Rotating Camera");
        Camera.rotateTo(yaw, pitch); // Dreambot handles smooth rotation
    }

    private void checkStats() {
        Logger.log("AntiBan: Checking Skills");
        if (Tabs.open(Tab.SKILLS)) {
            sleep(600, 100);
            Skills.hoverSkill(Skill.values()[random.nextInt(Skill.values().length)]);
            sleep(1200, 300);
        }
    }

    private void checkRandomTab() {
        List<Tab> distinctTabs = Arrays.asList(Tab.INVENTORY, Tab.EQUIPMENT, Tab.MAGIC, Tab.PRAYER, Tab.FRIENDS);
        Tab t = distinctTabs.get(random.nextInt(distinctTabs.size()));
        Logger.log("AntiBan: Checking tab " + t.name());
        Tabs.open(t);
        sleep(800, 200);
    }
    
    private void rightClickRandomEntity() {
        List<Entity> candidates = new ArrayList<>();
        
        // Add nearby entities
        candidates.addAll(NPCs.all(n -> n != null && n.distance(Players.getLocal()) < 7));
        candidates.addAll(GameObjects.all(o -> o != null && o.distance(Players.getLocal()) < 7 && o.getName() != null && !o.getName().equals("null")));
        candidates.addAll(Players.all(p -> p != null && !p.equals(Players.getLocal()) && p.distance(Players.getLocal()) < 7));
        
        if (candidates.isEmpty()) {
            Logger.log("AntiBan: No entities to right-click");
            return;
        }
        
        Entity target = candidates.get(random.nextInt(candidates.size()));
        if (target != null) {
            Logger.log("AntiBan: Right-clicking " + target.getName());
            if (target.isOnScreen()) {
                // Examine is a safe right-click action
                target.interact("Examine");
                sleep(600, 300);
            }
        }
    }
}
