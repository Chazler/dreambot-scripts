package com.allinone.framework;

import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.GameObject;

import com.allinone.skills.combat.data.LocationDef;
import com.allinone.skills.woodcutting.data.WoodcuttingSpot;
import com.allinone.skills.mining.data.MiningSpot;
import com.allinone.skills.fishing.data.FishingSpot;

/**
 * Shared state for the Behavior Tree
 */
public class Blackboard {

    // State variables
    private NPC currentTarget;
    private GameObject currentObject;
    private LocationDef bestLocation;
    private String currentStatus = "Initializing...";
    private boolean forceFiremakingRelocate = false;

    // Woodcutting
    private WoodcuttingSpot currentWoodcuttingSpot;

    // Mining
    private MiningSpot currentMiningSpot;

    // Firemaking
    private com.allinone.skills.firemaking.data.FiremakingLog currentFiremakingLog;

    // Fishing
    private FishingSpot currentFishingSpot;

    private final AntiBan antiBan = new AntiBan();

    // Timer
    private long timeRemaining;
    private boolean forceStopSkill = false;

    public Blackboard() {
    }

    /**
     * Resets all skill-specific state. Call when switching skills.
     */
    public void reset() {
        currentTarget = null;
        currentObject = null;
        bestLocation = null;
        currentWoodcuttingSpot = null;
        currentMiningSpot = null;
        currentFiremakingLog = null;
        currentFishingSpot = null;
        forceFiremakingRelocate = false;
        forceStopSkill = false;
        currentStatus = "Initializing...";
    }

    public com.allinone.skills.firemaking.data.FiremakingLog getCurrentFiremakingLog() { return currentFiremakingLog; }
    public void setCurrentFiremakingLog(com.allinone.skills.firemaking.data.FiremakingLog log) { this.currentFiremakingLog = log; }

    public boolean shouldForceStopSkill() { return forceStopSkill; }
    public void setForceStopSkill(boolean forceStopSkill) { this.forceStopSkill = forceStopSkill; }

    public long getTimeRemaining() { return timeRemaining; }
    public void setTimeRemaining(long timeRemaining) { this.timeRemaining = timeRemaining; }

    public FishingSpot getCurrentFishingSpot() { return currentFishingSpot; }
    public void setCurrentFishingSpot(FishingSpot spot) { this.currentFishingSpot = spot; }

    public WoodcuttingSpot getCurrentWoodcuttingSpot() {
        return currentWoodcuttingSpot;
    }

    public void setCurrentWoodcuttingSpot(WoodcuttingSpot spot) {
        this.currentWoodcuttingSpot = spot;
    }

    public MiningSpot getCurrentMiningSpot() { return currentMiningSpot; }
    public void setCurrentMiningSpot(MiningSpot spot) { this.currentMiningSpot = spot; }

    public AntiBan getAntiBan() {
        return antiBan;
    }

    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }

    public NPC getCurrentTarget() {
        return currentTarget;
    }

    public void setCurrentTarget(NPC currentTarget) {
        this.currentTarget = currentTarget;
    }

    public GameObject getCurrentObject() {
        return currentObject;
    }

    public void setCurrentObject(GameObject currentObject) {
        this.currentObject = currentObject;
    }

    public LocationDef getBestLocation() {
        return bestLocation;
    }

    public void setBestLocation(LocationDef bestLocation) {
        this.bestLocation = bestLocation;
    }

    public boolean isForceFiremakingRelocate() {
        return forceFiremakingRelocate;
    }

    public void setForceFiremakingRelocate(boolean forceFiremakingRelocate) {
        this.forceFiremakingRelocate = forceFiremakingRelocate;
    }
}
