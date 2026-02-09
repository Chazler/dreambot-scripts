package com.allinone.skills.smithing.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.skills.smithing.data.SmithingItem;
import com.allinone.skills.smithing.data.SmithingType;
import com.allinone.skills.smithing.strategies.SmithingManager;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.widgets.WidgetChild;

public class SmeltNode extends LeafNode {

    private final Blackboard blackboard;
    private long lastAnim = 0;

    public SmeltNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        SmithingItem item = SmithingManager.getBestItem();
        if (item.getType() != SmithingType.SMELTING) return Status.FAILURE;

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
             // Fallback: Press Space (Usually works for "Make All" or "Make")
             org.dreambot.api.input.Keyboard.type(" ");
             // Wait for the action to actually start to avoid thinking we failed and clicking furnace again
             Sleep.sleepUntil(() -> Players.getLocal().isAnimating(), 3000);
             return Status.RUNNING;
        }

        blackboard.setCurrentStatus("Smelting " + item.getProduceName());
        
        if (furnace.interact("Smelt")) {
            Sleep.sleepUntil(() -> {
                return Players.getLocal().isAnimating() || (Widgets.getWidget(270) != null && Widgets.getWidget(270).isVisible());
            }, 5000);
        }
        
        return Status.RUNNING;
    }
}
