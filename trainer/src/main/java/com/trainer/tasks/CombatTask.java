package com.trainer.tasks;

import com.trainer.framework.Task;
import com.trainer.antiban.Antiban;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.script.AbstractScript;

public class CombatTask implements Task {
    private final AbstractScript script;
    private final Antiban antiban;

    public CombatTask(AbstractScript script, Antiban antiban) {
        this.script = script;
        this.antiban = antiban;
    }

    @Override
    public boolean accept() {
        return Players.getLocal().isInCombat();
    }

    @Override
    public int execute() {
        // While fighting, we can perform antiban actions
        antiban.perform();

        // Check HP - if low (placeholder for future food logic)
        if (Players.getLocal().getHealthPercent() < 30) {
            script.log("Health is low!"); 
            // In future: eatFood();
        }

        // Wait a random amount of time to simulate human reaction/attention span
        return Calculations.random(600, 1500);
    }

    @Override
    public String status() {
        return "In Combat";
    }
}
