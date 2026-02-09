package com.trainer.tasks;

import com.trainer.framework.Task;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.wrappers.items.Item;

public class EatTask implements Task {
    private final AbstractScript script;

    public EatTask(AbstractScript script) {
        this.script = script;
    }

    @Override
    public boolean accept() {
        // Eat if HP is below 50% and we have food
        return Players.getLocal().getHealthPercent() < 50 && Inventory.contains(i -> i.hasAction("Eat"));
    }

    @Override
    public int execute() {
        Item food = Inventory.get(i -> i.hasAction("Eat"));
        if (food != null) {
            script.log("Eating " + food.getName() + " at " + Players.getLocal().getHealthPercent() + "% HP");
            if (food.interact("Eat")) {
                script.sleepUntil(() -> Players.getLocal().getHealthPercent() > 50, 1200, 100);
                return Calculations.random(600, 900);
            }
            return Calculations.random(400, 800);
        }
        return 600;
    }

    @Override
    public String status() {
        return "Eating Food";
    }
}
