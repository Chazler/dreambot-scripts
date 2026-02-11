package com.allinone.skills.smithing.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.skills.smithing.data.SmithingItem;
import com.allinone.skills.smithing.data.SmithingType;
import com.allinone.skills.smithing.strategies.SmithingManager;
import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;

public class SmeltNode extends LeafNode {

    private final Blackboard blackboard;
    private long lastAnim = 0;

    public SmeltNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        SmithingItem item = SmithingManager.getBestItem();
        if (item == null || item.getType() != SmithingType.SMELTING) return Status.FAILURE;

        if (Players.getLocal().isAnimating()) {
            lastAnim = System.currentTimeMillis();
        }

        if (System.currentTimeMillis() - lastAnim < 2500) {
            return Status.RUNNING;
        }

        GameObject furnace = GameObjects.closest("Furnace");
        if (furnace == null) return Status.FAILURE;

        // If interface is open
        if (Widgets.getWidget(270) != null && Widgets.getWidget(270).isVisible()) {
            blackboard.getAntiBan().sleep(300, 100);
            org.dreambot.api.input.Keyboard.type(" ");
            Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 3000);
            return Status.RUNNING;
        }

        blackboard.setCurrentStatus("Smelting " + item.getProduceName());

        // Camera: rotate to face furnace if off-screen
        if (!furnace.isOnScreen()) {
            Camera.rotateToEntity(furnace);
            blackboard.getAntiBan().sleep(400, 150);
        }

        if (furnace.interact("Smelt")) {
            blackboard.getAntiBan().sleep(600, 200);
            Sleep.sleepUntil(() -> {
                return Players.getLocal().isAnimating()
                    || (Widgets.getWidget(270) != null && Widgets.getWidget(270).isVisible());
            }, 5000);
        }

        return Status.RUNNING;
    }
}
