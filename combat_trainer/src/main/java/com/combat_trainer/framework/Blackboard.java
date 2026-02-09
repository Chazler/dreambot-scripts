package com.combat_trainer.framework;

import org.dreambot.api.wrappers.interactive.NPC;

import com.combat_trainer.data.GearItem;
import com.combat_trainer.data.LocationDef;

import java.util.List;

/**
 * Shared state for the Behavior Tree
 */
public class Blackboard {
    
    // State variables
    private NPC currentTarget;
    private LocationDef bestLocation;
    private List<GearItem> desiredGear;
    private boolean isBankingNeeded;
    private String currentStatus = "Initializing...";
    private boolean gearChecked = false;
    
    private final AntiBan antiBan = new AntiBan();

    public Blackboard() {
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
