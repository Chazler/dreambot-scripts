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
import org.dreambot.api.wrappers.widgets.WidgetChild;

public class SmithItemNode extends LeafNode {

    private final Blackboard blackboard;
    private long lastAnim = 0;
    private int interfaceFailCount = 0;

    public SmithItemNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        SmithingItem item = SmithingManager.getBestItem();
        if (item == null || item.getType() != SmithingType.FORGING) return Status.FAILURE;

        // Animation debounce - avoid spam-clicking anvil between smithing ticks
        if (Players.getLocal().isAnimating()) {
            lastAnim = System.currentTimeMillis();
            return Status.RUNNING;
        }

        if (System.currentTimeMillis() - lastAnim < 2500) {
            return Status.RUNNING;
        }

        GameObject anvil = GameObjects.closest("Anvil");
        if (anvil == null) return Status.FAILURE;

        // Check if interface is open (312 is common for smithing)
        if (Widgets.getWidget(312) != null && Widgets.getWidget(312).isVisible()) {
            WidgetChild child = null;
            if (Widgets.getWidget(312).getChildren() != null) {
                for (WidgetChild c : Widgets.getWidget(312).getChildren()) {
                    if (c != null && c.getName() != null
                            && c.getName().toLowerCase().contains(item.getProduceName().toLowerCase())) {
                        child = c;
                        break;
                    }
                }
            }

            if (child != null) {
                blackboard.getAntiBan().sleep(400, 150);
                if (child.interact("Smith All")) {
                    Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 3000);
                    interfaceFailCount = 0;
                    return Status.RUNNING;
                } else if (child.interact("Smith 1")) {
                    Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 3000);
                    interfaceFailCount = 0;
                    return Status.RUNNING;
                }
            } else {
                interfaceFailCount++;
                log("Could not find button for: " + item.getProduceName() + " (attempt " + interfaceFailCount + ")");
                if (interfaceFailCount >= 5) {
                    log("Failed to find smith button too many times - blacklisting item");
                    SmithingManager.blacklist(item);
                    interfaceFailCount = 0;
                }
            }
            return Status.RUNNING;
        }

        blackboard.setCurrentStatus("Forging " + item.getProduceName());

        // Camera: rotate to face anvil if off-screen
        if (!anvil.isOnScreen()) {
            Camera.rotateToEntity(anvil);
            blackboard.getAntiBan().sleep(400, 150);
        }

        if (anvil.interact("Smith")) {
            blackboard.getAntiBan().sleep(600, 200);
            Sleep.sleepUntil(() -> Widgets.getWidget(312) != null && Widgets.getWidget(312).isVisible(), 5000);
        }

        return Status.RUNNING;
    }
}
