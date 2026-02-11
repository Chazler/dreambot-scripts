package com.allinone.framework.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Generic node that banks items when inventory is full.
 * Replaces BankLogsNode, BankFishNode, BankOreNode.
 */
public class BankItemsNode extends LeafNode {

    private final Blackboard blackboard;
    private final Supplier<Boolean> shouldBankCheck;
    private final Predicate<Item> keepFilter;
    private final String statusMessage;

    /**
     * @param blackboard     Shared state
     * @param shouldBankCheck Returns true if this skill is configured to bank (vs drop)
     * @param keepFilter     Items matching this predicate are KEPT during deposit (tools, etc.)
     * @param statusMessage  Status message shown during banking
     */
    public BankItemsNode(Blackboard blackboard, Supplier<Boolean> shouldBankCheck,
                         Predicate<Item> keepFilter, String statusMessage) {
        this.blackboard = blackboard;
        this.shouldBankCheck = shouldBankCheck;
        this.keepFilter = keepFilter;
        this.statusMessage = statusMessage;
    }

    @Override
    public Status execute() {
        if (!Inventory.isFull()) {
            return Status.FAILURE;
        }

        if (!shouldBankCheck.get()) {
            return Status.FAILURE;
        }

        blackboard.setCurrentStatus(statusMessage);

        if (!Bank.isOpen()) {
            if (Bank.open()) {
                Sleep.sleepUntil(Bank::isOpen, 5000);
            }
            return Status.RUNNING;
        }

        if (Bank.depositAllExcept(i -> keepFilter.test(i))) {
            if (Inventory.isEmpty() || Inventory.onlyContains(i -> keepFilter.test(i))) {
                Bank.close();
                return Status.SUCCESS;
            }
        }

        return Status.RUNNING;
    }
}
