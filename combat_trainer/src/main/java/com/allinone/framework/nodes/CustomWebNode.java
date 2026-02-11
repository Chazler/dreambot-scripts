package com.allinone.framework.nodes;

import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.methods.map.Map;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.walking.web.node.impl.EntranceWebNode;
import org.dreambot.api.utilities.Sleep;
import java.util.function.BooleanSupplier;

public class CustomWebNode extends EntranceWebNode {

    private BooleanSupplier condition;

    public CustomWebNode(Tile tile, String entityName, String action) {
        super(tile.getX(), tile.getY(), tile.getZ());
        setEntityName(entityName);
        setAction(action);
    }

    public CustomWebNode(int x, int y, int z, String entityName, String action) {
        super(x, y, z);
        setEntityName(entityName);
        setAction(action);
    }

    @Override
    public boolean execute(AbstractWebNode nextNode) {
        if (getTile().distance() > 10) {
            Walking.walk(getTile());
            return false;
        }
        Entity entity = GameObjects.closest(getEntityName());
        if (entity == null) {
            entity = NPCs.closest(getEntityName());
        }
        if (entity != null) {
            if (Map.canReach(entity)) {
                if (entity.interact(getAction())) {
                    Sleep.sleepUntil(() -> getTile().distance() > 20
                            || Players.getLocal().getZ() != getTile().getZ(), 10000);
                }
            } else {
                Walking.walk(entity);
            }
        }
        return false;
    }

    public CustomWebNode from(AbstractWebNode node) {
        if (node != null) {
            node.addConnections(this);
        }
        return this;
    }

    public CustomWebNode to(AbstractWebNode node) {
        if (node != null) {
            this.addConnections(node);
        }
        return this;
    }

    public CustomWebNode condition(BooleanSupplier condition) {
        this.condition = condition;
        return this;
    }

    @Override
    public boolean hasRequirements() {
        return (condition == null || condition.getAsBoolean()) && super.hasRequirements();
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
