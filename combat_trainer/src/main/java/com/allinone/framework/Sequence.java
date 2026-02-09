package com.allinone.framework;

public class Sequence extends CompositeNode {

    public Sequence(Node... nodes) {
        super(nodes);
    }

    @Override
    public Status tick() {
        for (Node child : children) {
            Status status = child.tick();
            if (status != Status.SUCCESS) {
                return status; // Return FAILURE or RUNNING
            }
        }
        return Status.SUCCESS; // All children succeeded
    }
}
