package com.allinone.skills.smithing.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.framework.TravelHelper;
import com.allinone.skills.smithing.data.SmithingItem;
import com.allinone.skills.smithing.data.SmithingType;
import com.allinone.skills.smithing.strategies.SmithingManager;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.wrappers.interactive.GameObject;

public class TravelToSmithingLocationNode extends LeafNode {

    private final Blackboard blackboard;

    // Fallback locations
    private static final Area AL_KHARID_FURNACE = new Area(new Tile(3272, 3183, 0), new Tile(3278, 3189, 0));
    private static final Area VARROCK_ANVIL = new Area(new Tile(3183, 3422, 0), new Tile(3189, 3428, 0));

    public TravelToSmithingLocationNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        SmithingItem item = SmithingManager.getBestItem();
        String targetObjectName = (item.getType() == SmithingType.SMELTING) ? "Furnace" : "Anvil";

        GameObject nearest = GameObjects.closest(targetObjectName);

        if (nearest != null && nearest.distance(Players.getLocal()) < 10) {
            return Status.SUCCESS;
        }

        blackboard.setCurrentStatus("Walking to " + targetObjectName);

        if (nearest != null) {
            TravelHelper.travelTo(nearest.getTile().getArea(1));
        } else {
            // Fallback to known locations
            Area fallback = (item.getType() == SmithingType.SMELTING) ? AL_KHARID_FURNACE : VARROCK_ANVIL;
            TravelHelper.travelTo(fallback);
        }

        return Status.RUNNING;
    }
}
