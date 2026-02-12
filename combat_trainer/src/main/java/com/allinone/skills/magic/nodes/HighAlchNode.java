package com.allinone.skills.magic.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.skills.magic.data.MagicSpell;
import com.allinone.skills.magic.strategies.MagicManager;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.utilities.Sleep;

/**
 * High Level Alchemy node - casts High Alchemy on inventory items.
 * Used when level 55+ is reached and we have nature runes.
 */
public class HighAlchNode extends LeafNode {

    private final Blackboard blackboard;
    private long lastCastTime = 0;

    // Items worth alching (common drops/crafted items)
    private static final String[] ALCH_TARGETS = {
        "Rune platebody", "Rune platelegs", "Rune plateskirt",
        "Adamant platebody", "Adamant platelegs",
        "Mithril platebody", "Mithril platelegs",
        "Steel platebody", "Steel platelegs",
        "Iron platebody", "Iron platelegs",
        "Green d'hide body", "Blue d'hide body",
        "Yew longbow", "Maple longbow",
        "Gold bracelet", "Gold necklace", "Gold ring",
        "Nature rune" // Fallback - alch anything
    };

    public HighAlchNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        // Only use high alch if we have the level and runes
        if (org.dreambot.api.methods.skills.Skills.getRealLevel(org.dreambot.api.methods.skills.Skill.MAGIC) < 55) {
            return Status.FAILURE;
        }

        if (!Inventory.contains("Nature rune") || Inventory.count("Nature rune") < 1) {
            return Status.FAILURE;
        }

        // Need fire runes (or fire staff)
        boolean hasFireSource = Inventory.count("Fire rune") >= 5
            || (org.dreambot.api.methods.container.impl.equipment.Equipment.contains("Staff of fire"))
            || (org.dreambot.api.methods.container.impl.equipment.Equipment.contains("Fire battlestaff"))
            || (org.dreambot.api.methods.container.impl.equipment.Equipment.contains("Mystic fire staff"));

        if (!hasFireSource) {
            return Status.FAILURE;
        }

        // Cooldown between casts
        if (System.currentTimeMillis() - lastCastTime < 3200) {
            return Status.RUNNING;
        }

        // Find an item to alch
        String alchTarget = findAlchTarget();
        if (alchTarget == null) {
            return Status.FAILURE;
        }

        blackboard.setCurrentStatus("High Alching " + alchTarget);

        if (Tabs.getOpen() != Tab.MAGIC) {
            Tabs.open(Tab.MAGIC);
            blackboard.getAntiBan().sleep(300, 100);
        }

        if (Magic.castSpell(Normal.HIGH_LEVEL_ALCHEMY)) {
            blackboard.getAntiBan().sleep(200, 100);
            if (Inventory.interact(alchTarget, "Cast")) {
                lastCastTime = System.currentTimeMillis();
                blackboard.getAntiBan().sleep(600, 200);
            }
        }

        return Status.RUNNING;
    }

    private String findAlchTarget() {
        for (String target : ALCH_TARGETS) {
            if (Inventory.contains(target) && !target.equals("Nature rune")) {
                return target;
            }
        }
        return null;
    }
}
