package com.allinone.framework.nodes;

import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Sleep;

/**
 * Priority node that dismisses level-up dialogs, chatbox dialogues,
 * and other modal interfaces that block player actions.
 *
 * Returns SUCCESS when a dialog was found and dismissed (consuming the tick).
 * Returns FAILURE when no dialog is present (allowing the tree to continue).
 */
public class DismissDialogNode extends LeafNode {

    @Override
    public Status execute() {
        // Check for active dialogues (level-up, NPC chat, etc.)
        if (Dialogues.inDialogue()) {
            log("Dialog detected - dismissing");

            if (Dialogues.canContinue()) {
                Dialogues.continueDialogue();
                Sleep.sleepUntil(() -> !Dialogues.canContinue(), 2000);
            } else if (Dialogues.areOptionsAvailable()) {
                // If options are available, just close by pressing space to pick default
                org.dreambot.api.input.Keyboard.type(" ");
                Sleep.sleepUntil(() -> !Dialogues.inDialogue(), 2000);
            } else {
                // Generic continue attempt
                Dialogues.spaceToContinue();
                Sleep.sleepUntil(() -> !Dialogues.inDialogue(), 2000);
            }
            return Status.SUCCESS;
        }

        // Check for the "Please wait..." type of interface (widget 233)
        // which can appear during certain transitions
        if (isWidgetVisible(233)) {
            log("Wait dialog detected - waiting");
            Sleep.sleep(600);
            return Status.SUCCESS;
        }

        return Status.FAILURE;
    }

    private boolean isWidgetVisible(int widgetId) {
        return Widgets.getWidget(widgetId) != null && Widgets.getWidget(widgetId).isVisible();
    }
}
