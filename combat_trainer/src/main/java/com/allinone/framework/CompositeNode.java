package com.allinone.framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CompositeNode extends Node {
    protected List<Node> children;

    public CompositeNode(Node... nodes) {
        super();
        this.children = new ArrayList<>(Arrays.asList(nodes));
    }

    public void addChild(Node node) {
        children.add(node);
    }
    
    @Override
    public abstract Status tick();
}
