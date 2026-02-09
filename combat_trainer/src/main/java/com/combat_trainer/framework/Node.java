package com.combat_trainer.framework;

import org.dreambot.api.utilities.Logger;

public abstract class Node {
    
    public Node() {
    }

    public abstract Status tick();
    
    public void log(String message) {
        Logger.log("[" + this.getClass().getSimpleName() + "] " + message);
    }
}
