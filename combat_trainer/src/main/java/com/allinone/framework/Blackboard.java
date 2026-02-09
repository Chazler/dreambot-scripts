package com.allinone.framework;

import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.GameObject;

import com.allinone.skills.combat.data.GearItem;
import com.allinone.skills.combat.data.LocationDef;
import com.allinone.skills.woodcutting.data.WoodcuttingSpot;
import com.allinone.skills.fishing.data.FishingSpot;

import java.util.List;

/**
 * Shared state for the Behavior Tree
 */
public class Blackboard {
    
    // State variables
    private NPC currentTarget;
    private GameObject currentObject; // For Woodcutting/Mining
    private LocationDef bestLocation;
    private List<GearItem> desiredGear;
    private boolean isBankingNeeded;
    private String currentStatus = "Initializing...";
    private boolean gearChecked = false;
    
    // Woodcutting
    private WoodcuttingSpot currentWoodcuttingSpot;
    
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
    
    public AntiBan getAntiBan() {
        return antiBan;
    }

    public boolean isGearChecked() { return gearChecked; }
    public void setGearChecked(boolean gearChecked) { this.gearChecked = gearChecked; }

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

    public List<GearItem> getDesiredGear() {
        return desiredGear;
    }

    public void setDesiredGear(List<GearItem> desiredGear) {
        this.desiredGear = desiredGear;
    }

    public boolean isBankingNeeded() {
        return isBankingNeeded;
    }

    public void setBankingNeeded(boolean bankingNeeded) {
        isBankingNeeded = bankingNeeded;
    }
}
