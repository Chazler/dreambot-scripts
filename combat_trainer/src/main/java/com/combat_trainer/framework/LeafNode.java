package com.combat_trainer.framework;

public abstract class LeafNode extends Node {
    public LeafNode() {
        super();
    }
    
    // Helper to keep logic clean - can be overridden for more complex state
    public abstract Status execute();
    
    @Override
    public Status tick() {
        return execute();
    }
}
