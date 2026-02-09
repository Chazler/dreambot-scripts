package com.allinone.skills.firemaking.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.interactive.Players;
import com.allinone.framework.TravelHelper;
import org.dreambot.api.methods.container.impl.bank.BankLocation;

public class TravelToBurnAreaNode extends LeafNode {

    private final Blackboard blackboard;

    public TravelToBurnAreaNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        // ...Existing Logic for Area Selection...
        Area burnArea = new Area(3160, 3480, 3175, 3495); // GE Area generic
        Tile myLoc = Players.getLocal().getTile();
        if (BankLocation.DRAYNOR.getArea(5).contains(myLoc)) {
            burnArea = new Area(3092, 3240, 3100, 3246);
        } else if (BankLocation.VARROCK_WEST.getArea(5).contains(myLoc)) {
            burnArea = new Area(3180, 3435, 3192, 3442);
        } else if (BankLocation.SEERS.getArea(5).contains(myLoc)) {
            burnArea = new Area(2725, 3490, 2730, 3500); 
        } else {
            burnArea = new Area(3163, 3482, 3170, 3495); // GE Fire line
        }
        
        if (burnArea.contains(myLoc)) {
             return Status.SUCCESS;
        }
        
        blackboard.setCurrentStatus("Walking to Burn Area");
        // Use TravelHelper
        TravelHelper.travelTo(burnArea);
        
        return Status.RUNNING;
    }
}
