package com.allinone.skills.magic.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.framework.TravelHelper;
import com.allinone.skills.magic.data.MagicSpell;
import com.allinone.skills.magic.strategies.MagicManager;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Sleep;

public class BankMagicNode extends LeafNode {

    private final Blackboard blackboard;

    public BankMagicNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        MagicSpell spell = MagicManager.getBestCombatSpell();

        if (spell == null) {
            log("No valid magic spells available. Stopping skill.");
            blackboard.setForceStopSkill(true);
            return Status.FAILURE;
        }

        // Check if we have all required runes
        if (hasRequiredRunes(spell)) {
            return Status.SUCCESS;
        }

        // Need to bank for runes
        if (!Bank.isOpen()) {
            blackboard.setCurrentStatus("Banking for runes");
            Bank.open();
            return Status.RUNNING;
        }

        // Deposit non-rune items (keep runes and staff)
        for (org.dreambot.api.wrappers.items.Item item : Inventory.all()) {
            if (item != null && !item.getName().toLowerCase().contains("rune")
                && !item.getName().toLowerCase().contains("staff")) {
                Bank.deposit(item.getName());
                Sleep.sleep(300);
            }
        }

        // Withdraw runes
        withdrawRune(spell.getRune1(), spell.getRune1Amount() * 500);
        withdrawRune(spell.getRune2(), spell.getRune2Amount() * 500);
        withdrawRune(spell.getRune3(), spell.getRune3Amount() * 500);

        // Verify we got the runes
        if (!hasRequiredRunes(spell)) {
            log("Missing runes for " + spell.getSpellName() + ". Blacklisting.");
            MagicManager.blacklist(spell);
            return Status.RUNNING;
        }

        Bank.close();
        return Status.SUCCESS;
    }

    private boolean hasRequiredRunes(MagicSpell spell) {
        if (spell.getRune1() != null && Inventory.count(spell.getRune1()) < spell.getRune1Amount()) return false;
        if (spell.getRune2() != null && Inventory.count(spell.getRune2()) < spell.getRune2Amount()) return false;
        if (spell.getRune3() != null && Inventory.count(spell.getRune3()) < spell.getRune3Amount()) return false;
        return true;
    }

    private void withdrawRune(String runeName, int amount) {
        if (runeName == null || amount <= 0) return;
        if (Inventory.contains(runeName)) return;

        if (Bank.contains(runeName)) {
            int available = Bank.count(runeName);
            int toWithdraw = Math.min(amount, available);
            Bank.withdraw(runeName, toWithdraw);
            Sleep.sleepUntil(() -> Inventory.contains(runeName), 2000);
        }
    }
}
