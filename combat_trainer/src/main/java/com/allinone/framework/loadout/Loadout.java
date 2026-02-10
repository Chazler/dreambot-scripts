package com.allinone.framework.loadout;

import com.allinone.framework.ItemTarget;
import java.util.*;

import java.util.stream.Collectors;

public abstract class Loadout {

    private final Map<Slot, List<LoadoutItem>> slots = new HashMap<>();
    private final Map<Slot, String> overrides = new HashMap<>();
    private final List<ItemTarget> inventoryItems = new ArrayList<>();

    /**
     * Define a list of candidate items for a slot, ordered from BEST to WORST.
     * The manager will pick the first one you own and can use.
     */
    protected void setSlot(Slot slot, LoadoutItem... candidates) {
        slots.put(slot, Arrays.asList(candidates));
    }
    
    /**
     * Removes a slot requirement completely.
     */
    public void removeSlot(Slot slot) {
        slots.remove(slot);
        overrides.remove(slot);
    }

    /**
     * Add a specific item that must be in the inventory.
     */
    public void addInventoryItem(String name, int amount) {
        inventoryItems.add(new ItemTarget(name, amount, false));
    }

    public void addInventoryItem(String name) {
        addInventoryItem(name, 1);
    }

    /**
     * Compatibility overload for simple string lists (no requirements).
     */
    protected void setSlot(Slot slot, String... candidates) {
        List<LoadoutItem> items = Arrays.stream(candidates)
                .map(LoadoutItem::new)
                .collect(Collectors.toList());
        slots.put(slot, items);
    }

    /**
     * Force a specific item for this slot, ignoring any automated logic.
     */
    public void addOverride(Slot slot, String itemName) {
        overrides.put(slot, itemName);
    }
    
    // --- Internal Accessors ---

    public boolean hasOverride(Slot slot) {
        return overrides.containsKey(slot);
    }

    public String getOverride(Slot slot) {
        return overrides.get(slot);
    }

    public List<LoadoutItem> getCandidates(Slot slot) {
        return slots.getOrDefault(slot, Collections.emptyList());
    }

    public List<ItemTarget> getInventoryItems() {
        return inventoryItems;
    }

    public Set<Slot> getDefinedSlots() {
        Set<Slot> defined = new HashSet<>(slots.keySet());
        defined.addAll(overrides.keySet());
        return defined;
    }
}
