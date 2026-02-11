package com.allinone.framework.nodes;

import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.utilities.Logger;

public abstract class AbstractCustomNode extends CustomWebNode {

    private final Tile connectToTile;

    public AbstractCustomNode(Tile myTile, String name, String action, Tile connectToTile) {
        super(myTile, name, action);
        this.connectToTile = connectToTile;
    }

    public boolean register() {
        AbstractWebNode start = WebFinder.getWebFinder().getNearest(getTile(), 15);
        AbstractWebNode end = WebFinder.getWebFinder().getNearest(connectToTile, 15);
        
        if (start != null && end != null) {
            this.from(start).to(end);
            WebFinder.getWebFinder().addWebNode(this);
            Logger.log("Registered custom node: " + this.getClass().getSimpleName());
            return true;
        } else {
            Logger.log("Failed to register custom node: " + this.getClass().getSimpleName() + " (Start: " + (start != null) + ", End: " + (end != null) + ")");
            return false;
        }
    }
}
