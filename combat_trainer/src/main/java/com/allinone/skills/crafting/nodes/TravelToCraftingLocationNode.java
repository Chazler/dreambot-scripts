package com.allinone.skills.crafting.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.framework.TravelHelper;
import com.allinone.skills.crafting.data.CraftingItem;
import com.allinone.skills.crafting.data.CraftingType;
import com.allinone.skills.crafting.strategies.CraftingManager;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.wrappers.interactive.GameObject;

public class TravelToCraftingLocationNode extends LeafNode {

    private final Blackboard blackboard;

    // Furnace location for jewelry
    private static final Area AL_KHARID_FURNACE = new Area(new Tile(3272, 3183, 0), new Tile(3278, 3189, 0));

    public TravelToCraftingLocationNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        CraftingItem item = CraftingManager.getBestItem();
        if (item == null) return Status.FAILURE;

        // Leather and gem crafting can be done anywhere (bank standing)
        if (item.getType() == CraftingType.LEATHER || item.getType() == CraftingType.GEM) {
            return Status.SUCCESS;
        }

        // Jewelry needs a furnace
        if (item.getType() == CraftingType.JEWELRY) {
            GameObject furnace = GameObjects.closest("Furnace");
            if (furnace != null && furnace.distance(Players.getLocal()) < 10) {
                return Status.SUCCESS;
            }

            blackboard.setCurrentStatus("Walking to Furnace");
            if (furnace != null) {
                TravelHelper.travelTo(furnace.getTile().getArea(1));
            } else {
                TravelHelper.travelTo(AL_KHARID_FURNACE);
            }
            return Status.RUNNING;
        }

        return Status.SUCCESS;
    }
}
