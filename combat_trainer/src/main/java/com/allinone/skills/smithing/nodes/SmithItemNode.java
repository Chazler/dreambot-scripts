package com.allinone.skills.smithing.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.skills.smithing.data.SmithingItem;
import com.allinone.skills.smithing.data.SmithingType;
import com.allinone.skills.smithing.strategies.SmithingManager;
import org.dreambot.api.methods.input.Camera;

import java.util.List;

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

        // Check if smithing item selection interface is open (widget 312)
        if (Widgets.getWidget(312) != null && Widgets.getWidget(312).isVisible()) {
            WidgetChild child = findSmithingChild(item.getProduceName());

            if (child != null) {
                blackboard.getAntiBan().sleep(400, 150);
                
                // Directly interact with the widget (Left Click)
                if (child.interact()) {
                    Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 3000);
                    interfaceFailCount = 0;
                } else {
                    interfaceFailCount++;
                    log("Failed to interact with: " + item.getProduceName() + " (attempt " + interfaceFailCount + ")");
                }
            } else {
                interfaceFailCount++;
                log("Could not find button for: " + item.getProduceName() + " (attempt " + interfaceFailCount + ")");
            }

            if (interfaceFailCount >= 5) {
                log("Failed to find/interact smith button too many times - blacklisting item");
                SmithingManager.blacklist(item);
                interfaceFailCount = 0;
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
            Sleep.sleepUntil(() -> {
                return (Widgets.getWidget(312) != null && Widgets.getWidget(312).isVisible())
                    || (Widgets.getWidget(270) != null && Widgets.getWidget(270).isVisible());
            }, 5000);
        }

        return Status.RUNNING;
    }

    /**
     * Searches for the smithing widget child matching the item name.
     * Checks both direct children and nested children of widget 312.
     */
    private WidgetChild findSmithingChild(String itemName) {
        List<WidgetChild> children = Widgets.getWidget(312).getChildren();
        if (children == null) return null;

        String searchName = itemName.toLowerCase();

        // Helper to check a widget
        for (WidgetChild c : children) {
            if (c != null) {
                // Check Name
                if (c.getName() != null && c.getName().toLowerCase().contains(searchName)) {
                    return c;
                }
                // Check Actions (e.g. "Smith Bronze dagger")
                String[] actions = c.getActions();
                if (actions != null) {
                    for (String action : actions) {
                        if (action != null && action.toLowerCase().contains("smith " + searchName)) {
                            return c;
                        }
                    }
                }
            }
        }

        // Check nested children (some interfaces have deeper widget hierarchy)
        for (WidgetChild c : children) {
            if (c == null) continue;
            WidgetChild[] nested = c.getChildren();
            if (nested == null) continue;
            for (WidgetChild nc : nested) {
                if (nc != null) {
                    // Check Name
                    if (nc.getName() != null && nc.getName().toLowerCase().contains(searchName)) {
                        return nc;
                    }
                    // Check Actions
                    String[] actions = nc.getActions();
                    if (actions != null) {
                        for (String action : actions) {
                            if (action != null && action.toLowerCase().contains("smith " + searchName)) {
                                return nc;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }
}
