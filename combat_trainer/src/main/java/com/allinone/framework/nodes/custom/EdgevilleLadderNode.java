package com.allinone.framework.nodes.custom;

import com.allinone.framework.nodes.AbstractCustomNode;
import org.dreambot.api.methods.map.Tile;

public class EdgevilleLadderNode extends AbstractCustomNode {
    
    private static final Tile DUNGEON_LANDING_TILE = new Tile(3097, 9868, 0);
    private static final Tile TRAPDOOR_TILE = new Tile(3097, 3468, 0);

    public EdgevilleLadderNode() {
        super(DUNGEON_LANDING_TILE, "Ladder", "Climb-up", TRAPDOOR_TILE);
    }
}
