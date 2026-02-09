package com.trainer.framework;

import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.methods.map.Tile;
import java.util.HashMap;
import java.util.Map;

public class StuckManager {
    private Entity lastTarget;
    private int failureCount;
    private final Map<String, Long> blacklistedEntities = new HashMap<>(); // Key -> Expiry Time
    private static final long BLACKLIST_DURATION = 30_000; // 30 seconds
    private static final int MAX_FAILURES = 3;

    private String getEntityKey(Entity e) {
        if (e == null) return "";
        // Use Tile + ID/Name for uniqueness. 
        // Entities like GameObjects don't always have a unique index content.
        Tile t = e.getTile();
        return e.getName() + "_" + (t != null ? t.toString() : "null") + "_" + e.getID();
    }

    public boolean isBlacklisted(Entity entity) {
        if (entity == null) return false;
        String key = getEntityKey(entity);
        if (blacklistedEntities.containsKey(key)) {
            if (System.currentTimeMillis() > blacklistedEntities.get(key)) {
                blacklistedEntities.remove(key);
                return false;
            }
            return true;
        }
        return false;
    }

    public void trackFailure(Entity target) {
        if (target == null) return;
        String key = getEntityKey(target);
        String lastKey = getEntityKey(lastTarget);
        
        if (key.equals(lastKey)) {
            failureCount++;
            if (failureCount >= MAX_FAILURES) {
                System.out.println("[Failsafe] Blacklisting stuck entity: " + target.getName() + " at " + target.getTile());
                blacklistedEntities.put(key, System.currentTimeMillis() + BLACKLIST_DURATION);
                reset();
            }
        } else {
            lastTarget = target;
            failureCount = 1;
        }
    }

    public void reset() {
        failureCount = 0;
        lastTarget = null;
    }
}
