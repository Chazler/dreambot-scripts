package com.allinone.skills.cooking.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.skills.cooking.data.CookingItem;
import com.allinone.skills.cooking.data.CookingLocation;
import com.allinone.skills.cooking.strategies.CookingManager;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;

public class CookNode extends LeafNode {

    private final Blackboard blackboard;
    private long lastAnim = 0;

    public CookNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        CookingItem item = CookingManager.getBestItem();
        if (item == null) return Status.FAILURE;

        // No raw food left - need to bank
        if (!Inventory.contains(item.getRawName())) {
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

        CookingLocation loc = CookingManager.getBestLocation();
        if (loc == null) return Status.FAILURE;

        // If Make-All interface is open (widget 270), press space
        if (Widgets.getWidget(270) != null && Widgets.getWidget(270).isVisible()) {
            blackboard.getAntiBan().sleep(300, 100);
            org.dreambot.api.input.Keyboard.type(" ");
            Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 3000);
            return Status.RUNNING;
        }

        GameObject cookObj = GameObjects.closest(loc.getObjectName());
        if (cookObj == null) return Status.FAILURE;

        blackboard.setCurrentStatus("Cooking " + item.getRawName());

        // Rotate camera if needed
        if (!cookObj.isOnScreen()) {
            Camera.rotateToEntity(cookObj);
            blackboard.getAntiBan().sleep(400, 150);
        }

        // Use raw food on cooking object
        if (Inventory.interact(item.getRawName(), "Use")) {
            blackboard.getAntiBan().sleep(300, 100);
            if (cookObj.interact("Use")) {
                blackboard.getAntiBan().sleep(600, 200);
                Sleep.sleepUntil(() -> {
                    return Players.getLocal().isAnimating()
                        || (Widgets.getWidget(270) != null && Widgets.getWidget(270).isVisible());
                }, 5000);
            }
        }

        return Status.RUNNING;
    }
}
