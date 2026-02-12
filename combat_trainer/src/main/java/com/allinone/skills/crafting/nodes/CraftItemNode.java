package com.allinone.skills.crafting.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.skills.crafting.data.CraftingItem;
import com.allinone.skills.crafting.data.CraftingType;
import com.allinone.skills.crafting.strategies.CraftingManager;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;

public class CraftItemNode extends LeafNode {

    private final Blackboard blackboard;
    private long lastAnim = 0;

    public CraftItemNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        CraftingItem item = CraftingManager.getBestItem();
        if (item == null) return Status.FAILURE;

        // No materials left
        if (!Inventory.contains(item.getPrimaryItem())) {
            return Status.FAILURE;
        }

        // Animation debounce
        if (Players.getLocal().isAnimating()) {
            lastAnim = System.currentTimeMillis();
            return Status.RUNNING;
        }

        if (System.currentTimeMillis() - lastAnim < 2500) {
            return Status.RUNNING;
        }

        // Handle Make-All interface (widget 270)
        if (Widgets.getWidget(270) != null && Widgets.getWidget(270).isVisible()) {
            blackboard.getAntiBan().sleep(300, 100);
            org.dreambot.api.input.Keyboard.type(" ");
            Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 3000);
            return Status.RUNNING;
        }

        blackboard.setCurrentStatus("Crafting " + item.getProduceName());

        switch (item.getType()) {
            case LEATHER:
                return craftLeather(item);
            case GEM:
                return craftGem(item);
            case JEWELRY:
                return craftJewelry(item);
            default:
                return Status.FAILURE;
        }
    }

    private Status craftLeather(CraftingItem item) {
        // Use needle on leather
        if (Inventory.interact("Needle", "Use")) {
            blackboard.getAntiBan().sleep(300, 100);
            if (Inventory.interact(item.getPrimaryItem(), "Use")) {
                blackboard.getAntiBan().sleep(600, 200);
                Sleep.sleepUntil(() -> {
                    return Players.getLocal().isAnimating()
                        || (Widgets.getWidget(270) != null && Widgets.getWidget(270).isVisible());
                }, 5000);
            }
        }
        return Status.RUNNING;
    }

    private Status craftGem(CraftingItem item) {
        // Use chisel on uncut gem
        if (Inventory.interact("Chisel", "Use")) {
            blackboard.getAntiBan().sleep(300, 100);
            if (Inventory.interact(item.getPrimaryItem(), "Use")) {
                blackboard.getAntiBan().sleep(600, 200);
                Sleep.sleepUntil(() -> {
                    return Players.getLocal().isAnimating()
                        || (Widgets.getWidget(270) != null && Widgets.getWidget(270).isVisible());
                }, 5000);
            }
        }
        return Status.RUNNING;
    }

    private Status craftJewelry(CraftingItem item) {
        // Use gold bar on furnace
        GameObject furnace = GameObjects.closest("Furnace");
        if (furnace == null) return Status.FAILURE;

        if (!furnace.isOnScreen()) {
            Camera.rotateToEntity(furnace);
            blackboard.getAntiBan().sleep(400, 150);
        }

        if (Inventory.interact(item.getPrimaryItem(), "Use")) {
            blackboard.getAntiBan().sleep(300, 100);
            if (furnace.interact("Use")) {
                blackboard.getAntiBan().sleep(600, 200);
                Sleep.sleepUntil(() -> {
                    return Players.getLocal().isAnimating()
                        || (Widgets.getWidget(270) != null && Widgets.getWidget(270).isVisible())
                        || (Widgets.getWidget(446) != null && Widgets.getWidget(446).isVisible());
                }, 5000);
            }
        }
        return Status.RUNNING;
    }
}
