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

public class SmithItemNode extends LeafNode {

    private final Blackboard blackboard;

    public SmithItemNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        SmithingItem item = SmithingManager.getBestItem();
        if (item.getType() != SmithingType.FORGING) return Status.FAILURE;

        if (Players.getLocal().isAnimating()) return Status.RUNNING;

        GameObject anvil = GameObjects.closest("Anvil");
        if (anvil == null) return Status.FAILURE;

        // Check if interface is open (312 is common for smithing)
        if (Widgets.getWidget(312) != null && Widgets.getWidget(312).isVisible()) {
            
            WidgetChild child = null;
            if (Widgets.getWidget(312).getChildren() != null) {
                for (WidgetChild c : Widgets.getWidget(312).getChildren()) {
                    if (c != null && c.getName() != null && c.getName().toLowerCase().contains(item.getProduceName().toLowerCase())) {
                        child = c;
                        break;
                    }
                }
            }
            
            if (child != null) {
                if (child.interact("Smith All")) {
                    Sleep.sleep(1000);
                    return Status.RUNNING;
                } else if (child.interact("Smith 1")) { // Fallback
                     Sleep.sleep(1000);
                     return Status.RUNNING;
                }
            } else {
                log("Could not find button for: " + item.getProduceName());
            }
            return Status.RUNNING;
        }

        blackboard.setCurrentStatus("Forging " + item.getProduceName());
        
        if (anvil.interact("Smith")) {
             Sleep.sleepUntil(() -> Widgets.getWidget(312) != null && Widgets.getWidget(312).isVisible(), 5000);
        }
        
        return Status.RUNNING;
    }
}
