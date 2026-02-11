package com.allinone.framework.nodes.custom;

import com.allinone.framework.nodes.AbstractCustomNode;
import org.dreambot.api.methods.map.Tile;

public class LumbridgeSwampExitNode extends AbstractCustomNode {
    
    private static final Tile CAVE_LANDING = new Tile(3169, 9571, 0);
    private static final Tile SWAMP_ENTRANCE = new Tile(3169, 3173, 0);

    public LumbridgeSwampExitNode() {
        super(CAVE_LANDING, "Rope", "Climb-up", SWAMP_ENTRANCE);
    }
}
