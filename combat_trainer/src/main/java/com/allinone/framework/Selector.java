package com.allinone.framework;

public class Selector extends CompositeNode {

    public Selector(Node... nodes) {
        super(nodes);
    }

    @Override
    public Status tick() {
        for (Node child : children) {
            Status status = child.tick();
            if (status != Status.FAILURE) {
                return status; // Return SUCCESS or RUNNING
            }
        }
        return Status.FAILURE; // All children failed
    }
}
