package com.combat_trainer.framework;

import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Player;
// Mouse removed until resolved, using Camera/Tabs only for now

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.awt.Point;

public class AntiBan {
    
    private final Random random = new Random();
    private long lastAntiBanTime = System.currentTimeMillis();
    
    // Configurable intervals
    private int minInterval = 15000; // 15s
    private int maxInterval = 120000; // 2 mins
    private int nextInterval = 30000; // Start with 30s

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
        int sleepTime = (int) (random.nextGaussian() * stdDev + mean);
        if (sleepTime < 50) sleepTime = 50; // Hard limit to prevent 0 or negative
        // Logger.log("Sleeping " + sleepTime + "ms"); // Optional logging
        Sleep.sleep(sleepTime);
    }
    
    private void updateNextInterval() {
        nextInterval = random.nextInt(maxInterval - minInterval) + minInterval;
    }

    private void performRandomAction() {
        int roll = random.nextInt(100);
        Logger.log("AntiBan: Performing random action (Roll: " + roll + ")");
        
        if (roll < 30) {
            moveCamera();
        } else if (roll < 60) {
            checkStats();
        } else {
            // 40% chance to check tabs
            checkRandomTab();
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
}
